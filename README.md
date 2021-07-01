# Image segmentation on the Food201 dataset

#### UniLj Mobile Sensing class, winter 2020

This repository contains code for food image segmantation model. The model has been trained on the Food201 dataset produced at Google [[1]](#1) with the aim to run it on device in an Android application (see `andoroid-application` folder, which also features a video).

Paper summarizing the project is available in releases or [here](https://github.com/janarez/Food201/releases/download/1.0.0/final-paper.pdf).


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

## References
<a id="1">[1]</a> 
Im2Calories: towards an automated mobile vision food diary
