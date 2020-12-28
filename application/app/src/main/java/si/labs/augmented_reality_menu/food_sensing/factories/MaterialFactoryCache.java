package si.labs.augmented_reality_menu.food_sensing.factories;

import android.content.Context;

import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class MaterialFactoryCache {
    private final HashMap<Color, Material> materialCache;

    public MaterialFactoryCache() {
        this.materialCache = new HashMap<>();
    }

    public CompletableFuture<Material> makeOpaqueWithColor(Context context, Color color) {
        CompletableFuture<Material> returnedFuture;

        if (materialCache.containsKey(color)) {
            returnedFuture = new CompletableFuture<>();
            returnedFuture.complete(materialCache.get(color));
        } else {
            returnedFuture = MaterialFactory.makeTransparentWithColor(context, color).thenApply(material -> {
                materialCache.putIfAbsent(color, material);
                return material;
            });
        }
        return returnedFuture;
    }
}
