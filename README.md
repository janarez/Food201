# Image segmentation on the Food201 dataset
#### UniLj Mobile Sensing class, winter 2020

This repository contains code for food image segmantation model. The model has been trained on the Food201 dataset produced at Google [[1]](#1).

## Background

Food201 is a dataset of food images along with segmentation masks. There are total of 201 food classes. The dataset is derived from the wellknown Food101 classification dataset.

- Test set: 2439 images
- Train set: 9654 images

From my research (Scopus, Google Scholar), I haven't found any other paper apart from [[1]](#1) that would deal with this dataset.

## Goal

The goal is to train a segmenation model convertable to Tensorflow Lite. The model must run on mobile devices without substantial delay. 

This limits the scope of the project considerably. Most importantly, the two important architecture approaches employed in [[1]](#1) are not relevant.

1. CRF: To smooth the coarse segmentation CRF layer is used. However, this layer is not available in Tensorflow out of the box and requires building custom operations from C++. Although, open source libraries are available, it would still be required to define the ops for conversion to TF Lite.

2. Classification context: To further aid the model they use results of a pretrained classification model. Again, this is not a suitable approach for mobile devices.

## Results

Below are results from the original paper [[1]](#1).

CRF?|Context?|Accuracy | Recall | IoU
--- | --- |--- | --- | ---
0|0|0.71|0.30|0.19
1|0|0.74|0.32|0.22
0|1|0.74|0.32|0.23
1|1|0.76|0.33|0.25

Our preliminary results on validation data.

&nbsp;|Accuracy | Recall | IoU
---|--- | --- | ---
incl. background|0.61|-|0.09
excl. background|-|-|0.05
top 5 classes (excl. bgd) |-|-|0.14
true classes (excl. bgd) |-|-|0.47

## TODO

1. It is clear from the produced results that the main problem is that the models predicts extraneous labels with low counts. This problem could be address via CRFs and/or the classification context. However, we ruled both approaches out, so the main question is what to do instead.

2. Some image augmentations. Nice, but not as important given point 1. and 3.

3. Train to convergence. The preliminary model has not converged aftech 30 epochs. The model takes around 45 mins per epoch, which greatly restricts my experimentation possibilities given the time available.

## References
<a id="1">[1]</a> 
Im2Calories: towards an automated mobile vision food diary
