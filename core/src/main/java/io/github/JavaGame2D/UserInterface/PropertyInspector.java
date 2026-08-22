package io.github.JavaGame2D.UserInterface;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.JavaGame2D.Annotations.InspectorIgnore;
import io.github.JavaGame2D.SpriteData;
import io.github.JavaGame2D.Systems.EntityComponentManager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class PropertyInspector {
    private final Table contentTable;      // The table we'll populate with fields
    private final ScrollPane scrollPane;   // Wraps the table so it scrolls
    private final Skin skin;
    private final EntityComponentManager entityComponentManager;
    private final PopupManager popupManager;

    private int selectedEntityId = -1;      // Track what we're currently showing

    public PropertyInspector(Stage stage, EntityComponentManager entityComponentManager, Skin skin) {
        this.skin = skin;
        this.entityComponentManager = entityComponentManager;
        this.popupManager = new PopupManager(skin, stage);

        stage.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                popupManager.closePopup();
                // Return false so the event propagates to parent actors and the Stage
                return false;
            }
        });

        // Create the table that holds all the property rows
        contentTable = new Table(skin);
        contentTable.top().left();
        contentTable.pad(10);

        // Wrap it in a ScrollPane
        scrollPane = new AutoFocusScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false); // Horizontal scroll off, vertical on
    }

    // Returns the root Actor (ScrollPane) so we can add it to the Stage
    public Actor getActor() {
        return scrollPane;
    }

    // Call this when selection changes (or to clear it)
    public void inspectEntity(OptionalInt inspectionTarget) {
        if (!inspectionTarget.isPresent()){
            selectedEntityId = -1;
            rebuild();
            return;
        }
        int entityId = inspectionTarget.getAsInt();
        if (selectedEntityId != entityId) {
            selectedEntityId = entityId;
            rebuild();
        }
    }

    private void rebuild() {
        contentTable.clear();

        if (selectedEntityId == -1) {
            contentTable.add("No entity selected").colspan(2).left().pad(10);
            return;
        }

        // Get all components for this entity.
        Map<Class<?>, Object> components = entityComponentManager.getEntityComponents(selectedEntityId);

        if (components == null || components.isEmpty()) {
            contentTable.add("Entity has no components").colspan(2).left().pad(10);
            return;
        }

        // Add a header with the Entity ID
        contentTable.add("Entity ID: " + selectedEntityId).colspan(2).left().pad(10);
        contentTable.row();

        // For each component, recursively render its fields
        for (Map.Entry<Class<?>, Object> entry : components.entrySet()) {
            Class<?> componentClass = entry.getKey();
            Object componentInstance = entry.getValue();

            // Skip marker components (no fields)
            if (componentClass.getDeclaredFields().length == 0) {
                continue;
            }

            // Component header
            contentTable.add();
            contentTable.row();
            contentTable.add(new Label("--- " + componentClass.getSimpleName() + " ---", skin))
                .colspan(2).left().padTop(10).padBottom(5);
            contentTable.row();

            // Recursively render this component's fields
            renderComponentFields(componentInstance, componentClass, contentTable);
        }
    }

    private void renderComponentFields(Object componentInstance, Class<?> componentClass, Table table){
        Field[] fields = componentClass.getDeclaredFields();
        for (Field field : fields) {

            //renderField();

            // skip non-public fields
            if (!java.lang.reflect.Modifier.isPublic(field.getModifiers())) {
                continue;
            }
            // skip fields marked with @InspectorIgnore ---
            if (field.isAnnotationPresent(InspectorIgnore.class)) {
                continue;
            }

            Class<?> fieldType = field.getType();

            Label fieldLabel = new Label(field.getName() + ":", skin);
            fieldLabel.setEllipsis(true); // <-- This automatically adds "..." when text is too long

            fieldLabel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Only show popup if the text is actually truncated
                    if (isTruncated(fieldLabel)) {
                        popupManager.showFullNamePopup(fieldLabel, field.getName());
                    }
                }
            });

            // --- DISPATCH TO SPECIFIC RENDERERS ---
            try {
                field.setAccessible(true);
                Object value = field.get(componentInstance);

                if (fieldType == boolean.class || fieldType == Boolean.class) {
                    renderBooleanField(fieldLabel, (boolean) value, field, componentInstance);
                }
                else if (fieldType.isEnum()) {
                    renderEnumField(fieldLabel, (Enum<?>) value, field, componentInstance);
                }
                else if (fieldType == Vector2.class) {
                    renderVector2Field(fieldLabel, (Vector2) value, field, componentInstance);
                }
                else if (fieldType == SpriteData.class) {
                    renderComponentFields(value, SpriteData.class, table);
                }
                else if (isPrimitiveOrString(fieldType)) {
                    renderPrimitiveField(fieldLabel, field, componentInstance);
                }
                else {
                    // Fallback: show type name and "unsupported"
                    contentTable.add(fieldLabel).left().padRight(10).width(120);
                    contentTable.add(new Label("[Unsupported: " + fieldType.getSimpleName() + "]", skin)).left().padBottom(4);
                    contentTable.row();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void renderVector2Field(Label nameLabel, Vector2 vec, Field field, Object componentInstance) {
        // If the vector is null, show "null"
        if (vec == null) {
            contentTable.add(nameLabel).left().padRight(10).width(120);
            contentTable.add(new Label("null", skin)).left().padBottom(0);
            contentTable.row();
            return;
        }

        // Create a sub-table to hold x and y side-by-side
        Table subTable = new Table(skin);
        subTable.defaults().padBottom(0);

        // X field
        TextField xField = new TextField(Float.toString(vec.x), skin);
        xField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    vec.x = Float.parseFloat(xField.getText());
                } catch (NumberFormatException e) {
                    xField.setText(Float.toString(vec.x));
                }
            }
        });
        subTable.add(new Label("x:", skin)).left().padRight(5);
        subTable.add(xField).expandX().fillX();

        subTable.row();

        // Y field
        TextField yField = new TextField(Float.toString(vec.y), skin);
        yField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    vec.y = Float.parseFloat(yField.getText());
                } catch (NumberFormatException e) {
                    yField.setText(Float.toString(vec.y));
                }
            }
        });
        subTable.add(new Label("y:", skin)).left().padRight(5);
        subTable.add(yField).left().expandX().fillX();

        // Add to the main table
        //contentTable.add(nameLabel).left().padRight(10).width(80);
        contentTable.add(nameLabel).left().top().padTop(2).width(100);
        contentTable.add(subTable).left().expandX().fillX().padBottom(4);
        contentTable.row();
    }

    private void renderPrimitiveField(Label nameLabel, Field field, Object componentInstance) {
        String valueString = getFieldValueAsString(field, componentInstance);
        TextField valueField = new TextField(valueString, skin);
        valueField.setUserObject(new FieldBinding(field, componentInstance));
        valueField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TextField tf = (TextField) actor;
                FieldBinding binding = (FieldBinding) tf.getUserObject();
                applyFieldValue(binding.field, binding.component, tf.getText(), tf);
            }
        });

        contentTable.add(nameLabel).left().padRight(10).width(120);
        contentTable.add(valueField).left().expandX().fillX().padBottom(4);
        contentTable.row();
    }

    private void renderBooleanField(Label nameLabel, boolean currentValue, Field field, Object componentInstance) {
        // Create a CheckBox (empty label, we use the nameLabel for the description)
        CheckBox checkBox = new CheckBox("", skin);
        checkBox.setChecked(currentValue);

        // Add listener to update the component when toggled
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    field.setAccessible(true);
                    field.set(componentInstance, checkBox.isChecked());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        // Layout: Name in the left column, CheckBox in the right column
        contentTable.add(nameLabel).left().padRight(10).width(120);
        contentTable.add(checkBox).left().padBottom(4);
        contentTable.row();
    }

    private void renderEnumField(Label nameLabel, Enum<?> currentValue, Field field, Object componentInstance) {
        // Get all possible enum constants from the field's type
        Class<?> enumType = field.getType();
        Object[] enumConstants = enumType.getEnumConstants();

        if (enumConstants == null || enumConstants.length == 0) {
            // Should never happen for enums, but just in case
            contentTable.add(nameLabel).left().padRight(10).width(120);
            contentTable.add(new Label("[Empty Enum]", skin)).left().padBottom(4);
            contentTable.row();
            return;
        }

        // Create a SelectBox (dropdown) with the enum constants
        // LibGDX's SelectBox uses generics, so we cast to the enum type
        @SuppressWarnings("unchecked")
        SelectBox<Enum<?>> selectBox = new SelectBox<>(skin);
        selectBox.setItems((Enum<?>[]) enumConstants);

        // Select the current value (if not null)
        if (currentValue != null) {
            selectBox.setSelected(currentValue);
        } else {
            // If null, just select the first item (or leave unselected)
            // For safety, we can select the first item
            if (enumConstants.length > 0) {
                selectBox.setSelected((Enum<?>) enumConstants[0]);
            }
        }

        // Add listener to update the component
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    field.setAccessible(true);
                    field.set(componentInstance, selectBox.getSelected());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        // Layout
        contentTable.add(nameLabel).left().padRight(10).width(120);
        contentTable.add(selectBox).left().expandX().fillX().padBottom(4);
        contentTable.row();
    }

    private void renderObjectFields(String labelPrefix, Object obj, Table parentTable, Set<Object> visited) {

        if (obj == null) {
            parentTable.add(new Label(labelPrefix + "null", skin)).colspan(2).left().padBottom(2);
            parentTable.row();
            return;
        }

        // prevent endless loop due to circular references
        if (visited.contains(obj)){
            //parentTable.add(new Label(labelPrefix + "[Circular Reference]", skin)).colspan(2).left().padBottom(2);
            //parentTable.row();
            return;
        }
        visited.add(obj);

        Class<?> objClass = obj.getClass();
        Field[] fields = objClass.getDeclaredFields();

        // If the object has no fields, just show its toString()
        if (fields.length == 0) {
            parentTable.add(new Label(labelPrefix + obj.toString(), skin)).colspan(2).left().padBottom(2);
            parentTable.row();
            return;
        }

        // Add a section header (optional)
        parentTable.add(new Label(labelPrefix + "--- " + objClass.getSimpleName() + " ---", skin))
            .colspan(2).left().padTop(5).padBottom(2);
        parentTable.row();

        for (Field field : fields) {
            // only render public fields
            if (!Modifier.isPublic(field.getModifiers())){
                continue;
            }
            field.setAccessible(true); // this may be unnecessary

            try {
                Object value = field.get(obj);
                Class<?> type = field.getType();

                // --- Handle primitive types and Strings ---
                if (isPrimitiveOrString(type)) {
                    // Use a slightly indented label
                    Label nameLabel = new Label("  " + field.getName() + ":", skin);
                    String valueString = getFieldValueAsString(field, obj);
                    TextField valueField = new TextField(valueString, skin);
                    valueField.setUserObject(new FieldBinding(field, obj));
                    valueField.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            TextField tf = (TextField) actor;
                            FieldBinding binding = (FieldBinding) tf.getUserObject();
                            applyFieldValue(binding.field, binding.component, tf.getText(), tf);
                        }
                    });
                    parentTable.add(nameLabel).left().padRight(10).width(120);
                    parentTable.add(valueField).left().expandX().fillX().padBottom(2);
                    parentTable.row();
                }
                // --- Handle Vector2, Vector3, etc. (as before) ---
//                else if (type == Vector2.class) {
//                    // ... (your existing Vector2 handling, with indentation) ...
//                }
                // --- Recursive case: nested object ---
                else {
                    // If the value is null, show "null" and skip recursion
                    if (value == null) {
                        parentTable.add(new Label("  " + field.getName() + ": null", skin)).colspan(2).left().padBottom(2);
                        parentTable.row();
                    }
                    else {
                        // Recursively render the nested object with indentation
                        // We pass "  " as additional indentation prefix
                        parentTable.add(new Label("  " + field.getName(), skin)).colspan(2).left().padBottom(2);
                        renderObjectFields("    ", value, parentTable, visited);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isPrimitiveOrString(Class<?> type) {
        return type.isPrimitive() ||
            type == String.class ||
            type == Integer.class ||
            type == Float.class ||
            type == Double.class ||
            type == Long.class ||
            type == Byte.class ||
            type == Short.class;
    }


    // Helper to read the current value of a field as a String
    private String getFieldValueAsString(Field field, Object component) {
        try {
            field.setAccessible(true); // just in case
            Object value = field.get(component);
            if (value == null) return "null";
            return value.toString();
        } catch (IllegalAccessException e) {
            return "ERROR";
        }
    }

    // Helper to parse the text and write it back to the component field
    private void applyFieldValue(Field field, Object component, String text, TextField textField) {
        try {
            field.setAccessible(true);
            Class<?> type = field.getType();

            if (type == int.class || type == Integer.class) {
                field.set(component, Integer.parseInt(text));
            }
            else if (type == float.class || type == Float.class) {
                field.set(component, Float.parseFloat(text));
            }
            else if (type == boolean.class || type == Boolean.class) {
                field.set(component, Boolean.parseBoolean(text));
            }
            else if (type == String.class) {
                field.set(component, text);
            }
            else if (type == long.class || type == Long.class) {
                field.set(component, Long.parseLong(text));
            }
            else if (type == double.class || type == Double.class) {
                field.set(component, Double.parseDouble(text));
            }
            else {
                // For unsupported types, we just ignore editing.
                // You could add support for enums, or custom serializers later.
                System.out.println("Unsupported field type: " + type.getName());
            }
        } catch (NumberFormatException e) {
            // If parsing fails (e.g., "abc" for float), revert to the current value
            String currentValue = getFieldValueAsString(field, component);
            textField.setText(currentValue);
            System.out.println("Invalid input, reverted to: " + currentValue);
        } catch (IllegalAccessException e) {
            System.out.println("Failed to update field: " + e.getMessage());
        }
    }

    // Helper class to bundle field + component for the listener
    private class FieldBinding {
        final Field field;
        final Object component;

        FieldBinding(Field field, Object component) {
            this.field = field;
            this.component = component;
        }
    }

    //check if text inside label is truncated (tooLongText) -> (tooLon...)
    private boolean isTruncated(Label label){
        // 1. Get the font from the label's style
        BitmapFont font = label.getStyle().font;

        // 2. Measure full text width (without ellipsis)
        GlyphLayout measurement = new GlyphLayout();
        String fullText = label.getText().toString();
        measurement.setText(font, fullText);
        float actualTextWidth = measurement.width;

        // 3. Get the width of the text as actually rendered (with ellipsis if needed)
        float displayedWidth = label.getGlyphLayout().width;

        // 4. If displayed width is noticeably smaller, truncation occurred
        //    Tolerance of 1 pixel for floating-point / rounding errors
        return actualTextWidth - displayedWidth > 1f;
    }
}
