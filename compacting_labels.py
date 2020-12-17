# %% [markdown]
# # Labels
# This script compacts food labels as there are unnecessary skips and as a
# result we have 209 labels instead of the real 201.
#
# The compacting is done on masks as well as in the annotation files.

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
TRAIN_MASK_FOLDER = pathlib.Path(FOLDER + "/masks_train")
TEST_MASK_FOLDER = pathlib.Path(FOLDER + "/masks_test")

#%%
labels = pd.read_csv(f"{FOLDER}/pixel_annotations_map.csv", names=['id', 'food'])

# %%
# Use defaultdict since we need to map background to 0 and there are non mapped
# labels in masks as well (like 37).
id_map = defaultdict(int, {orig_id : new_id + 1 for new_id, orig_id in enumerate(labels['id'].values)})

# %%
labels['id'] = labels.index + 1

# Add background annotation.
ext_labels = pd.concat([pd.DataFrame({'id' : [0], 'food' : ['background']}), labels])

ext_labels.to_csv(f"{FOLDER}/labels.csv", index=False)
ext_labels['food'].to_csv(f"{FOLDER}/labels.txt", index=False)  # For the app.

# %% Remapping.
def remap_masks(old_folder, new_folder):
    masks = pathlib.Path(old_folder)

    for path in masks.glob('*/*.png'):
        image = PIL.Image.open(path)

        food_folder, food_name = path.parts[-2:]
        new_path = f"{new_folder}/{food_folder}/{food_name}"
        if not os.path.exists(f"{new_folder}/{food_folder}"):
            os.makedirs(f"{new_folder}/{food_folder}")
            print(f"Folder: {food_folder}")

        image_array = np.array(image)
        image_mapped = np.vectorize(id_map.__getitem__)(image_array)

        fixed_image = PIL.Image.fromarray(image_mapped.astype(np.uint8), mode='L')
        fixed_image.save(new_path)


# %%
# Remap all test masks.
remap_masks(TEST_MASK_FOLDER, f"{FOLDER}/new_masks_test")

# %%
# Remap all train masks.
remap_masks(TRAIN_MASK_FOLDER, f"{FOLDER}/new_masks_train")

