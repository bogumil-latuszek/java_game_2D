package io.github.JavaGame2D.UserInterface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import io.github.JavaGame2D.Components.DrawableComponent;
import io.github.JavaGame2D.EditScreen;
import io.github.JavaGame2D.Entity;
import io.github.JavaGame2D.Enums.RenderingLayer;
import io.github.JavaGame2D.Systems.EntityComponentManager;
import io.github.JavaGame2D.Systems.EntitySelector;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.OptionalInt;

/**
 * Docked panel showing all entities grouped by renderingLayer,
 * sorted by entityID within each group.
 *
 * Mirrors PropertyInspector's structure: a plain class wrapping a Table
 * inside a ScrollPane. Callers add getActor() to their layout.
 */
public class LevelHierarchy{

    private static final float ICON_WIDTH = 22f;
    private static final float INDENT = 14f;

    private final EditScreen editScreen;
    private final Skin skin;
    private final EntityComponentManager ecm;
    private final EditorVisibilityState visibility;
    private final EntitySelector selector;

    private final Table contentTable;
    private final ScrollPane scrollPane;

    // Pre-built drawables (avoid newDrawable calls during rebuild).
    private final Drawable layerHeaderBg;
    private final Drawable miscHeaderBg;
    private final Drawable highlightBg;

    // For cheap highlight updates without rebuilding the whole list.
    private final IntMap<Table> entityRows = new IntMap<>();
    private int highlightedEntity = -1;

    private final EnumSet<RenderingLayer> collapsedLayers = EnumSet.noneOf(RenderingLayer.class);

    public LevelHierarchy(EditScreen editScreen, EntityComponentManager ecm, EntitySelector selector, Skin skin, EditorVisibilityState visibility) {
        this.selector = selector;
        this.editScreen = editScreen;
        this.ecm = ecm;
        this.skin = skin;
        this.visibility = visibility;

        this.layerHeaderBg = skin.newDrawable("white", new Color(0f, 0f, 0f, 0.30f));
        this.miscHeaderBg  = skin.newDrawable("white", new Color(0f, 0f, 0f, 0.18f));
        this.highlightBg   = skin.newDrawable("white", new Color(0.25f, 0.45f, 0.75f, 0.75f));

        contentTable = new Table(skin);
        contentTable.top().left();
        contentTable.pad(4);

        scrollPane = new AutoFocusScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
    }

    public void resetCollapseState(){
        collapsedLayers.clear();
    }

    // ==============================================================
    //  Public API
    // ==============================================================

    public Actor getActor() {
        return scrollPane;
    }

    /** Called every frame by EditScreen. Rebuilds only when the dirty flag is set. */
    public void refreshIfDirty() {
        if (!editScreen.hierarchyDirty) return;
        rebuild();
        editScreen.hierarchyDirty = false;
    }

    /** Called by EditScreen when entitySelector.selectedEntity changes. */
    public void onSelectionChanged() {
        OptionalInt potentialSelectedID = selector.getSelectedEntityID();
        if (!potentialSelectedID.isPresent()){
            highlightedEntity = -1;
            return;
        }
        int selectedEntityID = potentialSelectedID.getAsInt();
        if (selectedEntityID == highlightedEntity) {
            return;
        }
        applyHighlight(highlightedEntity, false);
        applyHighlight(selectedEntityID, true);
        highlightedEntity = selectedEntityID;
    }

    // ==============================================================
    //  Rebuild
    // ==============================================================

    private void rebuild() {
        contentTable.clear();
        entityRows.clear();

        // Bucket entities by layer.
        EnumMap<RenderingLayer, IntArray> buckets = new EnumMap<>(RenderingLayer.class);
        for (RenderingLayer l : RenderingLayer.values()) buckets.put(l, new IntArray());
        IntArray misc = new IntArray();

        for (int id : ecm.getAllEntities()) {
            DrawableComponent dc = ecm.getComponent(DrawableComponent.class, id);
            if (dc != null) buckets.get(dc.renderingLayer).add(id);
            else misc.add(id);
        }

        // Sort each bucket by entityID ascending.
        for (IntArray arr : buckets.values()) arr.sort();
        misc.sort();

        // Emit layers in enum order.
        for (RenderingLayer layer : RenderingLayer.values()) {
            IntArray bucket = buckets.get(layer);
            boolean collapsed = collapsedLayers.contains(layer);

            addLayerHeader(layer, bucket.size, collapsed);

            if (!collapsed) {
                Table entityList = new Table(skin);
                entityList.top().left();
                for (int i = 0; i < bucket.size; i++) {
                    addEntityRow(entityList, layer, bucket.get(i));
                }
                contentTable.add(entityList).growX().row();
            }
        }

        // Misc at the bottom.
        addMiscHeader(misc.size);
        Table entityList = new Table(skin);
        entityList.top().left();
        for (int i = 0; i < misc.size; i++) {
            addEntityRow(entityList, null, misc.get(i));
        }
        contentTable.add(entityList).growX().row();

        highlightedEntity = -1;
        onSelectionChanged();
    }

    // ==============================================================
    //  Header / row builders
    // ==============================================================

    private void addLayerHeader(RenderingLayer layer, int count, boolean collapsed) {
        Table header = new Table(skin);
        header.setBackground(layerHeaderBg);

        Drawable expandedIcon = skin.getDrawable("expanded");
        Drawable collapsedIcon = skin.getDrawable("collapsed");
        ImageButton arrow = new ImageButton(collapsed ? collapsedIcon : expandedIcon);
        arrow.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (collapsedLayers.contains(layer)){
                    collapsedLayers.remove(layer);
                }
                else {
                    collapsedLayers.add(layer);
                }
                editScreen.hierarchyDirty = true;
            }
        });

        Drawable visibleIcon = skin.getDrawable("visible");
        Drawable hiddenIcon = skin.getDrawable("hidden");
        ImageButton eye = new ImageButton(visibility.isLayerVisible(layer) ? visibleIcon : hiddenIcon);
        eye.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean currentState = visibility.isLayerVisible(layer);
                visibility.setLayerVisible(layer, !currentState);
                editScreen.hierarchyDirty = true;
            }
        });

        Drawable locked = skin.getDrawable("locked");
        Drawable unlocked = skin.getDrawable("unlocked");
        ImageButton lock = new ImageButton(visibility.isLayerLocked(layer) ? locked : unlocked);
        lock.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean currentState = visibility.isLayerLocked(layer);
                visibility.setLayerLocked(layer, !currentState);
                editScreen.hierarchyDirty = true;
            }
        });

        header.add(arrow).width(ICON_WIDTH).padLeft(2);
        header.add(eye).width(ICON_WIDTH).padLeft(1);
        header.add(lock).width(ICON_WIDTH).padLeft(1);
        header.add(new Label(layer.name(), skin)).left().expandX();
        header.add(new Label("(" + count + ")", skin)).right().padRight(4);

        contentTable.add(header).growX().padTop(2).row();
    }

    private void addMiscHeader(int count) {
        Table header = new Table(skin);
        header.setBackground(miscHeaderBg);
        header.add(new Label("MISC", skin)).left().expandX().padLeft(2);
        header.add(new Label("(" + count + ")", skin)).right().padRight(4);
        contentTable.add(header).growX().padTop(2).row();
    }

    private void addEntityRow(Table parent, RenderingLayer layer, int entityID) {
        Table row = new Table(skin);

        boolean ownVisible = visibility.isEntityVisible(entityID);
        boolean ownLocked  = visibility.isEntityLocked(entityID);

        boolean effectiveVisible = (layer == null)
            ? ownVisible
            : visibility.isLayerVisible(layer) && ownVisible;
        boolean effectiveLocked = (layer == null)
            ? ownLocked
            : visibility.isLayerLocked(layer) || ownLocked;

        Drawable visibleIcon = skin.getDrawable("visible");
        Drawable hiddenIcon = skin.getDrawable("hidden");
        ImageButton eye = new ImageButton(visibility.isLayerVisible(layer) ? visibleIcon : hiddenIcon);
        eye.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean currentState = visibility.isLayerVisible(layer);
                visibility.setLayerVisible(layer, !currentState);
                editScreen.hierarchyDirty = true;
            }
        });

        Drawable locked = skin.getDrawable("locked");
        Drawable unlocked = skin.getDrawable("unlocked");
        ImageButton lock = new ImageButton(visibility.isLayerLocked(layer) ? locked : unlocked);
        lock.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean currentState = visibility.isLayerLocked(layer);
                visibility.setLayerLocked(layer, !currentState);
                editScreen.hierarchyDirty = true;
            }
        });

        Label idLabel = new Label(Integer.toString(entityID), skin);
        if (effectiveLocked) {
            idLabel.setColor(0.55f, 0.55f, 0.55f, 1f);
        } else if (!effectiveVisible) {
            idLabel.setColor(0.35f, 0.35f, 0.35f, 1f);
        }

        row.add(eye).width(ICON_WIDTH).padLeft(INDENT);
        row.add(lock).width(ICON_WIDTH).padLeft(1);
        row.add(idLabel).left().expandX().padRight(4);

        // Clicking the row body selects. The toggle buttons consume their own
        // clicks (they're separate actors), so toggling eye/lock won't fire this.
        row.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selector.setSelectedEntityID(entityID);
            }
        });

        entityRows.put(entityID, row);
        parent.add(row).growX().row();
    }

    // ==============================================================
    //  Helpers
    // ==============================================================

    /**
     * Placeholder toggle. Swap for ImageButton with eye/lock drawables
     * when the skin provides them.
     */
    private TextButton makeToggle(boolean initialState, BoolConsumer onToggle) {
        TextButton btn = new TextButton(initialState ? "O" : "-", skin);
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean newState = btn.getText().toString().equals("-");
                btn.setText(newState ? "O" : "-");
                onToggle.accept(newState);
            }
        });
        return btn;
    }

    private void applyHighlight(int entityID, boolean on) {
        if (entityID == -1) return;
        Table row = entityRows.get(entityID);
        if (row == null) return;
        row.setBackground(on ? highlightBg : null);
    }

    @FunctionalInterface
    public interface BoolConsumer {
        void accept(boolean value);
    }
}
