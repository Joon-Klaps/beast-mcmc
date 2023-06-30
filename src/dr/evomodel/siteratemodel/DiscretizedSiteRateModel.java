/*
 * DiscretizedSiteRateModel.java
 *
 * Copyright (c) 2002-2023 Alexei Drummond, Andrew Rambaut and Marc Suchard
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

package dr.evomodel.siteratemodel;

import dr.inference.model.*;
import dr.evomodel.substmodel.SubstitutionModel;

/**
 * DiscretizedSiteRateModel - A SiteModel that has a discrete categories of rates across sites.
 *
 * @author Andrew Rambaut
 * @version $Id: $
 */

public class DiscretizedSiteRateModel extends AbstractModel implements SiteRateModel {


    /**
     * Constructor for gamma+invar distributed sites. Either shapeParameter or
     * invarParameter (or both) can be null to turn off that feature.
     */
    public DiscretizedSiteRateModel(
            String name,
            Parameter nuParameter,
            double muWeight,
            SiteRateDelegate delegate) {

        super(name);

        this.nuParameter = nuParameter;
        if (nuParameter != null) {
            addVariable(nuParameter);
            nuParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        }
        this.muWeight = muWeight;

        addStatistic(muStatistic);

        this.delegate = delegate;
        addModel(delegate);

        categoryRates = new double[delegate.getCategoryCount()];
        categoryProportions = new double[delegate.getCategoryCount()];

        ratesKnown = false;
    }

    /**
     * set mu
     */
    public void setMu(double mu) {
        nuParameter.setParameterValue(0, mu / muWeight);
    }

    /**
     * @return mu
     */
    public final double getMu() {
        return nuParameter.getParameterValue(0) * muWeight;
    }

    // *****************************************************************
    // Interface SiteRateModel
    // *****************************************************************

    public int getCategoryCount() {
        return delegate.getCategoryCount();
    }

    public double[] getCategoryRates() {
        synchronized (this) {
            if (!ratesKnown) {
                calculateCategoryRates();
            }
        }

        return categoryRates;
    }

    public double[] getCategoryProportions() {
        synchronized (this) {
            if (!ratesKnown) {
                calculateCategoryRates();
            }
        }

        return categoryProportions;
    }

    public double getRateForCategory(int category) {
        synchronized (this) {
            if (!ratesKnown) {
                calculateCategoryRates();
            }
        }

        return categoryRates[category];
    }

    public double getProportionForCategory(int category) {
        synchronized (this) {
            if (!ratesKnown) {
                calculateCategoryRates();
            }
        }

        return categoryProportions[category];
    }

    private void calculateCategoryRates() {

        delegate.getCategories(categoryRates, categoryProportions);

        if (nuParameter != null) {
            double mu = getMu();
            for (int i = 0; i < getCategoryCount(); i++)
                categoryRates[i] *= mu;
        }

        ratesKnown = true;
    }

    // *****************************************************************
    // Interface ModelComponent
    // *****************************************************************

    protected void handleModelChangedEvent(Model model, Object object, int index) {
        // delegate has changed so fire model changed event
        listenerHelper.fireModelChanged(this, object, index);
    }

    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        if (variable == nuParameter) {
            ratesKnown = false; // MAS: I changed this because the rate parameter can affect the categories if the parameter is in siteModel and not clockModel
        } else {
            throw new RuntimeException("Unknown variable in DiscretizedSiteRateModel.handleVariableChangedEvent");
        }
        listenerHelper.fireModelChanged(this, variable, index);
    }

    protected void storeState() {
    } // no additional state needs storing

    protected void restoreState() {
        ratesKnown = false;
    }

    protected void acceptState() {
    } // no additional state needs accepting


    private Statistic muStatistic = new Statistic.Abstract() {

        public String getStatisticName() {
            return "mu";
        }

        public int getDimension() {
            return 1;
        }

        public String getDimensionName(int dim) {
            return getId();
        }

        public double getStatisticValue(int dim) {
            return getMu();
        }

    };


    /**
     * mutation rate parameter
     */
    private final Parameter nuParameter;

    private final double muWeight;

    private boolean ratesKnown;

    private final double[] categoryRates;

    private final double[] categoryProportions;

    private final SiteRateDelegate delegate;

    // This is here solely to allow the GammaSiteModelParser to pass on the substitution model to the
    // HomogenousBranchSubstitutionModel so that the XML will be compatible with older BEAST versions. To be removed
    // at some point.
    public SubstitutionModel getSubstitutionModel() {
        return substitutionModel;
    }

    public void setSubstitutionModel(SubstitutionModel substitutionModel) {
        this.substitutionModel = substitutionModel;
    }

    private SubstitutionModel substitutionModel;

}