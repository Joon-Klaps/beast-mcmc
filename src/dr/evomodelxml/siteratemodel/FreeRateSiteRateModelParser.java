/*
 * FreeRateSiteRateModelParser.java
 *
 * Copyright (c) 2002-2023 BEAST Development Team
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

package dr.evomodelxml.siteratemodel;

import dr.evomodel.siteratemodel.DiscretizedSiteRateModel;
import dr.evomodel.siteratemodel.FreeRateDelegate;
import dr.evomodel.substmodel.SubstitutionModel;
import dr.inference.model.Parameter;
import dr.oldevomodel.sitemodel.SiteModel;
import dr.xml.*;

import java.util.logging.Logger;

/**
 * This is a FreeRateSiteRateModelParser that uses the modular
 * DiscretizedSiteRateModel with a FreeRates delegate.
 * @author Andrew Rambaut
 * @version $Id$
 */
public class FreeRateSiteRateModelParser extends AbstractXMLObjectParser {

    public static final String FREE_RATE_SITE_RATE_MODEL = "freeRateSiteRateModel";
    public static final String MUTATION_RATE = "mutationRate";
    public static final String SUBSTITUTION_RATE = "substitutionRate";
    public static final String RELATIVE_RATE = "relativeRate";
    public static final String WEIGHT = "weight";
    public static final String RATES = "relativeRates";
    public static final String RATE_CATEGORIES = "rateCategories";
    public static final String WEIGHTS = "weights";

    public String getParserName() {
        return FREE_RATE_SITE_RATE_MODEL;
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        String msg = "";
        SubstitutionModel substitutionModel;

        double muWeight = 1.0;

        Parameter muParam = null;
        if (xo.hasChildNamed(SUBSTITUTION_RATE)) {
            muParam = (Parameter) xo.getElementFirstChild(SUBSTITUTION_RATE);

            msg += "\n  with initial substitution rate = " + muParam.getParameterValue(0);
        } else if (xo.hasChildNamed(RELATIVE_RATE)) {
            XMLObject cxo = xo.getChild(RELATIVE_RATE);
            muParam = (Parameter) cxo.getChild(Parameter.class);
            msg += "\n  with initial relative rate = " + muParam.getParameterValue(0);
            if (cxo.hasAttribute(WEIGHT)) {
                muWeight = cxo.getDoubleAttribute(WEIGHT);
                msg += " with weight: " + muWeight;
            }
        }

        int catCount = 4;
        XMLObject cxo = xo.getChild(RATES);
        catCount = cxo.getIntegerAttribute(RATE_CATEGORIES);
        Parameter ratesParameter = (Parameter)xo.getChild(Parameter.class);

        Parameter weightsParameter = (Parameter)xo.getElementFirstChild(WEIGHTS);

        msg += "\n  " + catCount + " category discrete free rate site rate heterogeneity model)";
        if (msg.length() > 0) {
            Logger.getLogger("dr.evomodel").info("\nCreating free rate site rate model: " + msg);
        } else {
            Logger.getLogger("dr.evomodel").info("\nCreating free rate site rate model.");
        }

        FreeRateDelegate delegate = new FreeRateDelegate("FreeRateDelegate", catCount, ratesParameter, weightsParameter);

        return new DiscretizedSiteRateModel(SiteModel.SITE_MODEL, muParam, muWeight, delegate);
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public String getParserDescription() {
        return "A DiscretizedSiteRateModel that has freely distributed rates across sites";
    }

    @Override
    public String[] getParserNames() {
        return super.getParserNames();
    }

    public Class<DiscretizedSiteRateModel> getReturnType() {
        return DiscretizedSiteRateModel.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private final XMLSyntaxRule[] rules = {
            AttributeRule.newIntegerRule(RATE_CATEGORIES, true),
            new XORRule(
                    new ElementRule(SUBSTITUTION_RATE, new XMLSyntaxRule[]{
                            new ElementRule(Parameter.class)
                    }),
                    new ElementRule(RELATIVE_RATE, new XMLSyntaxRule[]{
                            AttributeRule.newDoubleRule(WEIGHT, true),
                            new ElementRule(Parameter.class)
                    }), true
            ),

            new ElementRule(RATES, new XMLSyntaxRule[]{
                    AttributeRule.newIntegerRule(RATE_CATEGORIES, false),
                    new ElementRule(Parameter.class)
            }, false),

            new ElementRule(WEIGHTS, new XMLSyntaxRule[]{
                    new ElementRule(Parameter.class)
            }, false)
    };

}//END: class
