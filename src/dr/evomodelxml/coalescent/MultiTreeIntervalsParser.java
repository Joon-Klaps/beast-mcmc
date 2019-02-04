/*
 * MultiTreeIntervalsParser.java
 *
 * Copyright (c) 2002-2019 Alexei Drummond, Andrew Rambaut and Marc Suchard
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

package dr.evomodelxml.coalescent;

import dr.evolution.tree.Tree;
import dr.evolution.tree.TreeUtils;
import dr.evolution.util.Taxa;
import dr.evolution.util.TaxonList;
import dr.evomodel.coalescent.*;
import dr.evomodel.tree.TreeModel;
import dr.xml.*;

import java.util.ArrayList;
import java.util.List;

/**
 * MultiTreeIntervalsParser
 *
 * @author Andrew Rambaut
 */
public class MultiTreeIntervalsParser extends AbstractXMLObjectParser {

    public static final String MULTI_TREE_INTERVALS = "multiTreeIntervals";
    public static final String TREES = "trees";
    public static final String SINGLETONS = "singletons";
    public static final String CUTOFF = "cutoff";

    public String getParserName() {
        return MULTI_TREE_INTERVALS;
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        double cutoff = xo.getDoubleAttribute(CUTOFF);

        XMLObject cxo = xo.getChild(TREES);

        List<Tree> trees = new ArrayList<Tree>();

        for (int i = 0; i < cxo.getChildCount(); i++) {
            Tree tree = (Tree)cxo.getChild(Tree.class);
            trees.add(tree);
        }

        Taxa singletonTaxa = (Taxa)xo.getElementFirstChild(SINGLETONS);

        return new MultiTreeIntervals(trees, singletonTaxa, cutoff);
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public String getParserDescription() {
        return "This element stores the coalescent intervals across a set of trees.";
    }

    public Class getReturnType() {
        return CoalescentLikelihood.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private final XMLSyntaxRule[] rules = {
            AttributeRule.newDoubleRule(CUTOFF, false),

            new ElementRule(TREES, new XMLSyntaxRule[] {
                    new ElementRule(TreeModel.class, 1, Integer.MAX_VALUE)
            }, "Tree(s) to compute intervals for for", false),

            new ElementRule(SINGLETONS, new XMLSyntaxRule[]{
                    new ElementRule(Taxa.class)
            }, "An optional set of taxa which represent singleton trees", true)
    };
}
