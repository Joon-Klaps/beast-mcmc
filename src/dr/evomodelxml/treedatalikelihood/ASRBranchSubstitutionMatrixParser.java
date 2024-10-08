/*
 * ASRBranchSubstitutionMatrixParser.java
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

package dr.evomodelxml.treedatalikelihood;

import dr.evolution.datatype.DataType;
import dr.evolution.tree.TreeUtils;
import dr.evolution.util.Taxa;
import dr.evolution.util.TaxonList;
import dr.evomodel.branchratemodel.BranchRateModel;
import dr.evomodel.substmodel.FrequencyModel;
import dr.evomodel.substmodel.SubstitutionModel;
import dr.evomodel.treedatalikelihood.discrete.ASRSubstitutionModelConvolutionStatistic;
import dr.evomodel.treedatalikelihood.discrete.ASRBranchSubstitutionMatrix;
import dr.evomodel.treedatalikelihood.discrete.SequenceDistanceStatistic;
import dr.evomodel.treelikelihood.AncestralStateBeagleTreeLikelihood;
import dr.evoxml.util.DataTypeUtils;
import dr.inference.distribution.ParametricDistributionModel;
import dr.inference.model.Statistic;
import dr.xml.*;

import static dr.evomodelxml.tree.MonophylyStatisticParser.parseTaxonListOrTaxa;

/**
 */
public class ASRBranchSubstitutionMatrixParser extends AbstractXMLObjectParser {

    public static String STATISTIC = "asrBranchSubstitutionMatrix";
    private static final String MRCA = "mrca";
    public static final String TAXA = "taxa";
    public static final String DOUBLETS = "doublets";

    public String getParserName() { return STATISTIC; }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        String name = xo.getAttribute(Statistic.NAME, xo.getId());

        AncestralStateBeagleTreeLikelihood asrLike = (AncestralStateBeagleTreeLikelihood) xo.getChild(AncestralStateBeagleTreeLikelihood.class);

        DataType dataType = DataTypeUtils.getDataType(xo);

        if (dataType == null) dataType = (DataType) xo.getChild(DataType.class);

        boolean doublets = false;
        if (xo.hasAttribute(DOUBLETS)) {
            doublets = xo.getBooleanAttribute(DOUBLETS);
        }
        TaxonList mrcaTaxa = null;
        if (xo.hasChildNamed(MRCA)) {
            mrcaTaxa = parseTaxonListOrTaxa(xo.getChild(MRCA));
        }

        ASRBranchSubstitutionMatrix stat = null;
        try {
            stat = new ASRBranchSubstitutionMatrix(
                    name,
                    asrLike,
                    dataType,
                    doublets,
                    mrcaTaxa);
        } catch (TreeUtils.MissingTaxonException e) {
            throw new XMLParseException("Unable to find taxon-set.");
        }

        return stat;
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public String getParserDescription() {
        return "Takes ASR at an ancestral and descendant node and tracks empirical matrix of (from,to) state pairs (possibly for doublets).";
    }

    public Class getReturnType() { return SequenceDistanceStatistic.class; }

    public XMLSyntaxRule[] getSyntaxRules() { return rules; }

    private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
            new ElementRule(AncestralStateBeagleTreeLikelihood.class, false),
            new ElementRule(MRCA,
                    new XMLSyntaxRule[]{new ElementRule(Taxa.class)}, false),
    };

}

