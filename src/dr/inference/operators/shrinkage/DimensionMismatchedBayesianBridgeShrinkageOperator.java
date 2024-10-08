/*
 * DimensionMismatchedBayesianBridgeShrinkageOperator.java
 *
 * Copyright © 2002-2024 the BEAST Development Team
 * http://beast.community/about
 *
 * This file is part of BEAST.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership and licensing.
 *
 * BEAST is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 *  BEAST is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BEAST; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA
 *
 */

package dr.inference.operators.shrinkage;

import dr.inference.distribution.shrinkage.BayesianBridgeStatisticsProvider;
import dr.inference.model.Parameter;
import dr.inference.operators.GibbsOperator;
import dr.inference.operators.SimpleMCMCOperator;
import dr.math.distributions.GammaDistribution;

// A BayesianBridgeShrinkageOperator for the case where the Bridge prior applies to only some of the coefficients and not all
// That is, the dimension of the coefficients is assumed to be dim, while the dimension of the bridge is effectiveDim
// The discrepancy is handled by the mask
public class DimensionMismatchedBayesianBridgeShrinkageOperator extends SimpleMCMCOperator implements GibbsOperator {

    private final BayesianBridgeStatisticsProvider provider;
    private final BayesianBridgeShrinkageOperator operator;
    private final Parameter globalScale;
    private final Parameter localScale;
    private final Parameter regressionExponent;
    private final Parameter mask;
    private final int dim;
    private final int effectiveDim;

    private final GammaDistribution globalScalePrior;

    public DimensionMismatchedBayesianBridgeShrinkageOperator(BayesianBridgeStatisticsProvider bayesianBridge,
                                           GammaDistribution globalScalePrior,
                                           Parameter mask,
                                           BayesianBridgeShrinkageOperator operator,
                                           double weight) {

        setWeight(weight);

        this.provider = bayesianBridge;
        this.globalScale = bayesianBridge.getGlobalScale();
        this.localScale = bayesianBridge.getLocalScale();
        this.regressionExponent = bayesianBridge.getExponent();
        this.mask = mask;
        this.dim = bayesianBridge.getDimension();
        this.effectiveDim = getEffectiveDim();
        this.operator = operator;

        this.globalScalePrior = globalScalePrior;
    }

    @Override
    public String getOperatorName() {
        return "dimensionMismatchedBayesianBridgeGibbsOperator";
    }

    @Override
    public double doOperation() {

        if (globalScalePrior != null) {
            sampleGlobalScale(); // Order matters
        }

        if (localScale != null) {
            sampleLocalScale();
        }

        return 0;
    }

    private boolean exists(int index) {
        return mask == null || mask.getParameterValue(index) == 1.0;
    }

    private int getEffectiveDim() {
        int d = 0;
        for (int i = 0; i < dim; i++) {
            if (exists(i)) {
                d++;
            }
        }
        return d;
    }

    private int[] getDimensionMap() {
        int[] map = new int[dim];

        int d = 0;
        for (int i = 0; i < dim; i++) {
            if (exists(i)) {
                map[i] = d;
                d++;
            } else {
                map[i] = -1;
            }
        }
        return map;
    }

    private double absSumBeta() {
        double exponent = regressionExponent.getParameterValue(0);
        double sum = 0.0;
        for (int i = 0; i < dim; ++i) {
            if (exists(i)) {
                sum += Math.pow(Math.abs(provider.getCoefficient(i)), exponent);
            }
        }

        return sum;
    }

    public void sampleGlobalScale() {
        double draw = operator.drawGlobalScale(globalScalePrior.getShape(), globalScalePrior.getScale(), regressionExponent.getParameterValue(0), effectiveDim, absSumBeta());
        globalScale.setParameterValue(0, draw);
    }

    private void sampleLocalScale() {
        final int[] map = getDimensionMap();
        final double exponent = regressionExponent.getParameterValue(0);
        final double global = globalScale.getParameterValue(0);

        for (int i = 0; i < dim; ++i) {

            if (exists(i)) {
                double draw = operator.drawSingleLocalScale(global, exponent, provider.getCoefficient(i));
                localScale.setParameterValueQuietly(map[i], draw);
            }
        }

        localScale.fireParameterChangedEvent();
    }
}
