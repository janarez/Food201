# %% [markdown]
# # Labels
# This script check food labels in the compacted masks.
#
# There were several problems regarding saving the `.png`'s from numpy array.
# Tensorflow `decode_png` still cannot handle the `PIL` saved image, even though
# now it should indeed be a 1 channel 8 bit grayscale image. So the masks have
# to be loaded via `PIL` and wrap inside `py_function`. Shape must be explicitly
# ensured.

#%%
import pathlib
import pandas as pd 
import PIL
from PIL import Image
import numpy as np
from collections import defaultdict
import os

#%%
FOLDER = "../food201"
TRAIN_MASK_FOLDER = pathlib.Path(FOLDER + "/new_masks_train")
TRAIN_MASK_ORIG_FOLDER = pathlib.Path(FOLDER + "/masks_train")

TEST_MASK_FOLDER = pathlib.Path(FOLDER + "/new_masks_test")
TEST_MASK_ORIG_FOLDER = pathlib.Path(FOLDER + "/masks_test")


# %%
pixel_counts = defaultdict(int)

# %% Remapping.
def check_masks(folder, orig_folder):
    masks = pathlib.Path(folder)

    for path in masks.glob('*/*.png'):
        image = PIL.Image.open(path)
        image_array = np.array(image)
        vals, counts = np.unique(image_array, return_counts=True)

        if max(vals) == 0:
            print(path, image_array.shape)
            print(vals, counts)
            food_folder, food_name = path.parts[-2:]
            orig_path = f"{orig_folder}/{food_folder}/{food_name}"

            orig_image = PIL.Image.open(orig_path)
            orig_image_array = np.array(orig_image)
            orig_vals, orig_counts = np.unique(orig_image_array, return_counts=True)
            print(orig_vals)
            print(orig_counts)

        for v, c in zip(vals, counts):
            pixel_counts[v] += c


# %%
# Check all test masks.
check_masks(TEST_MASK_FOLDER, TEST_MASK_ORIG_FOLDER)
max(pixel_counts.keys())

# %%
# Check all train masks.
check_masks(TRAIN_MASK_FOLDER, TRAIN_MASK_ORIG_FOLDER)
max(pixel_counts.keys())

# %%
