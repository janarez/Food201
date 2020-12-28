package si.labs.augmented_reality_menu.food_sensing.factories;

import android.content.Context;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.RenderableDefinition;
import com.google.ar.sceneform.rendering.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RectangleFactory {
    private final MaterialFactoryCache materialFactory;

    public RectangleFactory(MaterialFactoryCache materialFactory) {
        this.materialFactory = materialFactory;
    }

    public CompletableFuture<ModelRenderable> getSquare(Context context, List<Node> corners, Color color) {
        return materialFactory.makeOpaqueWithColor(context, color)
                .thenCompose(material -> getSquare(corners, material));
    }

    public CompletableFuture<ModelRenderable> getSquare(List<Node> corners, Material material) {
        if (corners.size() != 4) {
            throw new IllegalArgumentException("Four corners are needed for a square");
        }

        Vector3 p0 = corners.get(0).getLocalPosition();
        Vector3 p1 = corners.get(1).getLocalPosition();
        Vector3 p2 = corners.get(2).getLocalPosition();
        Vector3 p3 = corners.get(3).getLocalPosition();
        Vector3 normal = Vector3.up();

        Vertex.UvCoordinate uvTopLeft = new Vertex.UvCoordinate(0, 1);
        Vertex.UvCoordinate uvTopRight = new Vertex.UvCoordinate(1, 1);
        Vertex.UvCoordinate uvBotLeft = new Vertex.UvCoordinate(0, 0);
        Vertex.UvCoordinate uvBotRight = new Vertex.UvCoordinate(1, 0);

        List<Vertex> vertices = new ArrayList<>(Arrays.asList(
                Vertex.builder().setPosition(p0).setNormal(normal).setUvCoordinate(uvBotLeft).build(),
                Vertex.builder().setPosition(p1).setNormal(normal).setUvCoordinate(uvTopLeft).build(),
                Vertex.builder().setPosition(p2).setNormal(normal).setUvCoordinate(uvTopRight).build(),
                Vertex.builder().setPosition(p3).setNormal(normal).setUvCoordinate(uvBotRight).build()
        ));

        List<Integer> triangleIndices = new ArrayList<>(Arrays.asList(
                0, 1, 3, 1, 2, 3
        ));

        RenderableDefinition.Submesh submesh = RenderableDefinition.Submesh.builder()
                .setTriangleIndices(triangleIndices)
                .setMaterial(material)
                .build();
        RenderableDefinition renderableDefinition = RenderableDefinition.builder()
                .setVertices(vertices)
                .setSubmeshes(Collections.singletonList(submesh))
                .build();

        return ModelRenderable.builder()
                .setSource(renderableDefinition)
                .build();
    }
}
