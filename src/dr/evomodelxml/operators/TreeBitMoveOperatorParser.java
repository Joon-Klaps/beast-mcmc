/*
 * TreeBitMoveOperatorParser.java
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

package dr.evomodelxml.operators;

import dr.evomodel.operators.TreeBitMoveOperator;
import dr.evomodel.tree.DefaultTreeModel;
import dr.evomodel.tree.TreeModel;
import dr.inference.operators.MCMCOperator;
import dr.xml.*;

/**
 */
public class TreeBitMoveOperatorParser extends AbstractXMLObjectParser {

    public static final String BIT_MOVE_OPERATOR = "treeBitMoveOperator";
    public static final String INDICTATOR_TRAIT = "indicatorTrait";
    public static final String TRAIT2 = "trait2";

    public String getParserName() {
        return BIT_MOVE_OPERATOR;
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        double weight = xo.getDoubleAttribute(MCMCOperator.WEIGHT);

        DefaultTreeModel treeModel = (DefaultTreeModel) xo.getChild(DefaultTreeModel.class);


        String trait1 = null;
        String trait2 = null;
        if (xo.hasAttribute(INDICTATOR_TRAIT)) trait1 = xo.getStringAttribute(INDICTATOR_TRAIT);
        if (xo.hasAttribute(TRAIT2)) trait2 = xo.getStringAttribute(TRAIT2);

        return new TreeBitMoveOperator(treeModel, trait1, trait2, weight);
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public String getParserDescription() {
        return "This element returns a bit-move operator on a given parameter.";
    }

    public Class getReturnType() {
        return MCMCOperator.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
            AttributeRule.newDoubleRule(MCMCOperator.WEIGHT),
            new ElementRule(DefaultTreeModel.class),
            AttributeRule.newStringRule(INDICTATOR_TRAIT, true),
            AttributeRule.newStringRule(TRAIT2, true)
    };

}
