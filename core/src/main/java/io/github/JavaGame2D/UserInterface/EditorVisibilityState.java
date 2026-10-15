package io.github.JavaGame2D.UserInterface;

import com.badlogic.gdx.utils.IntSet;
import io.github.JavaGame2D.Enums.RenderingLayer;

import java.util.EnumSet;
import java.util.Set;

/**
 * Transient editor state: which layers and entities are hidden or locked.
 * Reset on every level load. Never serialized to disk.
 *
 * Two independent flags per layer and per entity, combined with AND:
 *   hidden = layerHidden OR entityHidden
 *   locked = layerLocked OR entityLocked
 *
 * "Locked" means: rendered but not pickable by EntitySelector.
 * It does NOT block selection from the LevelHierarchyTab (Unity convention).
 */
public class EditorVisibilityState {

    private final Set<RenderingLayer> hiddenLayers = EnumSet.noneOf(RenderingLayer.class);
    private final Set<RenderingLayer> lockedLayers = EnumSet.noneOf(RenderingLayer.class);
    private final IntSet hiddenEntities = new IntSet();
    private final IntSet lockedEntities = new IntSet();
    // ---------- Layer toggles ----------

    public boolean isLayerVisible(RenderingLayer layer) { return !hiddenLayers.contains(layer); }
    public boolean isLayerLocked(RenderingLayer layer)  { return lockedLayers.contains(layer); }

    public void setLayerVisible(RenderingLayer layer, boolean visible) {
        if (visible) hiddenLayers.remove(layer); else hiddenLayers.add(layer);
    }
    public void setLayerLocked(RenderingLayer layer, boolean locked) {
        if (locked) lockedLayers.add(layer); else lockedLayers.remove(layer);
    }

    // ---------- Entity toggles ----------

    public boolean isEntityVisible(int entityID) { return !hiddenEntities.contains(entityID); }
    public boolean isEntityLocked(int entityID)  { return lockedEntities.contains(entityID); }

    public void setEntityVisible(int entityID, boolean visible) {
        if (visible) hiddenEntities.remove(entityID); else hiddenEntities.add(entityID);
    }
    public void setEntityLocked(int entityID, boolean locked) {
        if (locked) lockedEntities.add(entityID); else lockedEntities.remove(entityID);
    }

    // ---------- Combined queries ----------

    /** For entities WITH a DrawableComponent (they know their layer). */
    public boolean isVisible(int entityID, RenderingLayer layer) {
        return isLayerVisible(layer) && isEntityVisible(entityID);
    }
    public boolean isSelectable(int entityID, RenderingLayer layer) {
        return isVisible(entityID, layer) && !isLayerLocked(layer) && !isEntityLocked(entityID);
    }

    /** For entities WITHOUT a DrawableComponent (Misc group). */
    public boolean isSelectableNoLayer(int entityID) {
        return isEntityVisible(entityID) && !isEntityLocked(entityID);
    }

    public void reset() {
        hiddenLayers.clear();
        lockedLayers.clear();
        hiddenEntities.clear();
        lockedEntities.clear();
    }

    public Set<RenderingLayer> getHiddenLayers() {
        return hiddenLayers;
    }

    public Set<RenderingLayer> getLockedLayers() {
        return lockedLayers;
    }

    public IntSet getHiddenEntities() {
        return hiddenEntities;
    }

    public IntSet getLockedEntities() {
        return lockedEntities;
    }
}
