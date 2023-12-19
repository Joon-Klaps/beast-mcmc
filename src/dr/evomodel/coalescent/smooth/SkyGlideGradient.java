/*
 * SkyGlideLikelihood.java
 *
 * Copyright (c) 2002-2022 Alexei Drummond, Andrew Rambaut and Marc Suchard
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
 */


package dr.evomodel.coalescent.smooth;

import dr.inference.hmc.GradientWrtParameterProvider;
import dr.inference.model.Likelihood;
import dr.inference.model.Parameter;
import dr.xml.Reportable;

/**
 * A likelihood function for a piece-wise linear log population size coalescent process that nicely works with the newer tree intervals
 *
 * @author Mathieu Fourment
 * @author Erick Matsen
 * @author Xiang Ji
 * @author Marc A. Suchard
 */
public class SkyGlideGradient implements GradientWrtParameterProvider, Reportable {

    private final SkyGlideLikelihood likelihood;

    private final Parameter parameter;

    private final WrtParameter wrtParameter;

    private final double tolerance;

    public SkyGlideGradient(SkyGlideLikelihood likelihood,
                            Parameter parameter,
                            double tolerance) {
        this.likelihood = likelihood;
        this.parameter = parameter;
        this.wrtParameter = factory(parameter);
        this.tolerance = tolerance;
    }

    private WrtParameter factory(Parameter parameter) {
        if (parameter == likelihood.getLogPopSizeParameter()) {
            return WrtParameter.LOG_POP_SIZE;
        } else {
            throw new RuntimeException("Parameter not recognized.");
        }
    }

    @Override
    public Likelihood getLikelihood() {
        return likelihood;
    }

    @Override
    public Parameter getParameter() {
        return parameter;
    }

    @Override
    public int getDimension() {
        return parameter.getDimension();
    }

    @Override
    public double[] getGradientLogDensity() {
        return wrtParameter.getGradientLogDensity(likelihood);
    }

    @Override
    public String getReport() {
        return GradientWrtParameterProvider.getReportAndCheckForError(this, wrtParameter.getParameterLowerBound(), wrtParameter.getParameterUpperBound(), tolerance);
    }

    public enum WrtParameter {
        LOG_POP_SIZE {
            @Override
            double[] getGradientLogDensity(SkyGlideLikelihood likelihood) {
                return likelihood.getGradientWrtLogPopulationSize();
            }

            @Override
            double getParameterLowerBound() {
                return Double.NEGATIVE_INFINITY;
            }

            @Override
            double getParameterUpperBound() {
                return Double.POSITIVE_INFINITY;
            }
        };
        abstract double[] getGradientLogDensity(SkyGlideLikelihood likelihood);
        abstract double getParameterLowerBound();
        abstract double getParameterUpperBound();
    }
}
