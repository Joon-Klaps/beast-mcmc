/*
 * TreeTipGradient.java
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

package dr.evomodel.treedatalikelihood.continuous;

import dr.evolution.tree.Tree;
import dr.evolution.tree.TreeTrait;
import dr.evomodel.treedatalikelihood.TreeDataLikelihood;
import dr.evomodel.treedatalikelihood.preorder.NewTipFullConditionalDistributionDelegate;
import dr.evomodel.treedatalikelihood.preorder.TipFullConditionalDistributionDelegate;
import dr.evomodel.treedatalikelihood.preorder.TipGradientViaFullConditionalDelegate;
import dr.inference.hmc.GradientWrtParameterProvider;
import dr.inference.model.Likelihood;
import dr.inference.model.Parameter;
import dr.xml.Reportable;

/**
 * @author Marc A. Suchard
 */
public class TreeTipGradient implements GradientWrtParameterProvider, Reportable {

    private final TreeDataLikelihood treeDataLikelihood;
    private final TreeTrait treeTraitProvider;
    private final Tree tree;
    private final Parameter traitParameter;

    private final int nTaxa;
    private final int nTraits;
    private final int dimTrait;

    private final Parameter maskParameter;
    private final int gradientOffset;

    public TreeTipGradient(String traitName,
                           Parameter specifiedParameter,
                           TreeDataLikelihood treeDataLikelihood,
                           ContinuousDataLikelihoodDelegate likelihoodDelegate,
                           Parameter maskParameter) {

        assert (treeDataLikelihood != null);

        this.treeDataLikelihood = treeDataLikelihood;
        this.tree = treeDataLikelihood.getTree();
        this.maskParameter = maskParameter;

        String name =
                TipGradientViaFullConditionalDelegate.getName(traitName);

        // TODO Move into different constructor / parser
        String fcdName = TipFullConditionalDistributionDelegate.getName(traitName);
        if (treeDataLikelihood.getTreeTrait(fcdName) == null) {
            likelihoodDelegate.addFullConditionalDensityTrait(traitName);
        }

        String nFcdName = NewTipFullConditionalDistributionDelegate.getName(traitName);
        if (treeDataLikelihood.getTreeTrait(nFcdName) == null) {
            likelihoodDelegate.addNewFullConditionalDensityTrait(traitName);
        }


        nTaxa = treeDataLikelihood.getTree().getExternalNodeCount();
//        nTraits = treeDataLikelihood.getDataLikelihoodDelegate().getTraitCount();
//        dimTrait = treeDataLikelihood.getDataLikelihoodDelegate().getTraitDim();

//        PrecisionType precisionType = likelihoodDelegate.getPrecisionType();
//        int dimPartial = precisionType.getMatrixLength(dimTrait);


        int offset = 0;
        ContinuousTraitPartialsProvider dataModel = likelihoodDelegate.getDataModel();

        if (specifiedParameter == null) {
            specifiedParameter = dataModel.getParameter();
            if (dataModel.getDataDimension() != dataModel.getTraitDimension()) {
                throw new RuntimeException("Not currently implemented with unspecified parameter and dimension " +
                        "reduction.");
            }
        } else {
            if (specifiedParameter != dataModel.getParameter()) {
                ContinuousTraitPartialsProvider[] childModels = dataModel.getChildModels();
                // TODO: recurse child models
                for (int i = 0; i < childModels.length; i++) {
                    dataModel = childModels[i];
                    if (dataModel.getParameter() == specifiedParameter) {
                        break;
                    }
                    offset += dataModel.getTraitDimension();
                }
            }
        }

        if (specifiedParameter != dataModel.getParameter()) {
            throw new RuntimeException("Supplied parameter does not match the parameter in the data model" +
                    " or any of its submodels.");
        }

        this.traitParameter = dataModel.getParameter();
        this.dimTrait = dataModel.getTraitDimension();
        this.nTraits = dataModel.getTraitCount();
        this.gradientOffset = offset;

        if (nTraits != 1) {
            throw new RuntimeException("Not yet implemented for >1 traits");
        }

        if (maskParameter != null &&
                (maskParameter.getDimension() != traitParameter.getDimension())) {
            throw new RuntimeException("Trait and mask parameters must be the same size");
        }

        TreeTrait test = treeDataLikelihood.getTreeTrait(name);

        if (test == null) {
            likelihoodDelegate.addFullConditionalGradientTrait(traitName, gradientOffset, dimTrait);
        }

        treeTraitProvider = treeDataLikelihood.getTreeTrait(name);

        assert (treeTraitProvider != null);

    }

    @Override
    public Likelihood getLikelihood() {
        return treeDataLikelihood;
    }

    @Override
    public Parameter getParameter() {
        return traitParameter;
    }

    @Override
    public int getDimension() {
        return getParameter().getDimension();
    }

    @Override
    public double[] getGradientLogDensity() {

        double[] gradient = new double[nTaxa * dimTrait * nTraits];

        int offsetOutput = 0;
        for (int taxon = 0; taxon < nTaxa; ++taxon) {
            double[] taxonGradient = (double[]) treeTraitProvider.getTrait(tree, tree.getExternalNode(taxon));
            System.arraycopy(taxonGradient, 0, gradient, offsetOutput, dimTrait);
            offsetOutput += dimTrait;
        }

        if (maskParameter != null) {
            for (int i = 0; i < maskParameter.getDimension(); ++i) {
                if (maskParameter.getParameterValue(i) == 0.0) {
                    gradient[i] = 0.0;
                }
            }
        }

        return gradient;
    }

    @Override
    public String getReport() {
        return GradientWrtParameterProvider.getReportAndCheckForError(this, 0, Double.POSITIVE_INFINITY, tolerance);
    }

    private double tolerance = 1E-3;
}
