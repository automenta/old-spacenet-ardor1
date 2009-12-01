/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.control.pointer;

import automenta.spacenet.run.ArdorSpaceTime;
import automenta.spacenet.space.control.Draggable;
import automenta.spacenet.space.control.Pressable;
import automenta.spacenet.space.control.Tangible;
import automenta.spacenet.space.control.Touchable;
import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.input.GrabbedState;
import com.ardor3d.input.MouseButton;
import com.ardor3d.input.MouseManager;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.MouseButtonPressedCondition;
import com.ardor3d.input.logical.MouseButtonReleasedCondition;
import com.ardor3d.input.logical.MouseMovedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.intersection.PickData;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.intersection.PickingUtil;
import com.ardor3d.intersection.PrimitivePickResults;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector2;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class ArdorPointer {

    private PrimitivePickResults pickResults;
    private Touchable currentlyPicked;
    private Tangible currentTangible;
    private Pressable currentlyPressed;
    private PickData tangiblePick;
    private final ArdorSpaceTime spacetime;
    private Draggable currentDraggable, beingDragged;
    Vector2 pixelPos = new Vector2();
    final Ray3 pickRay = new Ray3();

    Vector2 pixelPosDragStart = new Vector2();

    final Ray3 rayDrag = new Ray3();
    final Ray3 rayDragStart = new Ray3();
    final Ray3 rayDragStop = new Ray3();
    Vector2 pixelPosDragStop  = new Vector2();

    protected void setRay(Vector2 pixelPos, Ray3 targetRay) {
        getCanvas().getCanvasRenderer().getCamera().getPickRay(pixelPos, false, targetRay);
    }
    protected void moved() {
        setRay(pixelPos, pickRay);

        doPick(pickRay);

        if (beingDragged!=null) {
            dragged();
        }
        else if (currentDraggable!=null) {
            //System.out.println(" drag dist: " + pixelPosDragStart.distance(pixelPos) + " @ " + pixelPos + " <- " + pixelPosDragStart);
            double dist = pixelPosDragStart.distance(pixelPos);
            if (dist > getDragThreshold()) {
                dragStart();
            }
        } 
        else {
            pixelPosDragStart.set(pixelPos);
        }


    }

    protected void dragStart() {
        setRay(pixelPos, rayDragStart);
        beingDragged = currentDraggable;

        beingDragged.startDrag(rayDragStart);
    }
    
    protected void dragged() {
        setRay(pixelPos, rayDrag);
        beingDragged.continueDrag(rayDrag);
    }
    protected void dragStop() {
        pixelPosDragStop.set(pixelPos);
        setRay(pixelPosDragStop, rayDragStop);

        beingDragged.stopDrag(rayDragStop);
    }

    protected double getDragThreshold() { return 1.01; }

    protected void leftPressed() {
//        if (getMouseManager().isSetGrabbedSupported()) {
//            getMouseManager().setGrabbed(GrabbedState.GRABBED);
//        }

        if (currentTangible instanceof Pressable) {
            currentlyPressed = (Pressable) currentTangible;
            ((Pressable) currentTangible).pressStart(tangiblePick);
        }

        if (currentTangible instanceof Draggable) {
            if (currentDraggable!=currentTangible) {
                currentDraggable = (Draggable) currentTangible;
                //System.out.println("start drag " + pixelPos);
                pixelPosDragStart.set(pixelPos);
            }
        }

    }

    protected void leftReleased() {
//        if (getMouseManager().isSetGrabbedSupported()) {
//            getMouseManager().setGrabbed(GrabbedState.NOT_GRABBED);
//        }

        if (currentlyPressed != null) {
            currentlyPressed.pressStop(tangiblePick);
            currentlyPressed = null;
        }

        currentDraggable = null;
        
        if (beingDragged!=null) {
            dragStop();
        }
        beingDragged = null;


    }

    public ArdorPointer(ArdorSpaceTime spacetime) {
        super();

        this.spacetime = spacetime;

        // Set up a reusable pick results
        pickResults = new PrimitivePickResults();
        pickResults.setCheckDistance(true);


        //        _logicalLayer.registerTrigger(new InputTrigger(new MouseButtonClickedCondition(MouseButton.RIGHT),
//            new TriggerAction() {
//
//                public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
//
//                    final Vector2 pos = Vector2.fetchTempInstance().set(
//                        inputStates.getCurrent().getMouseState().getX(),
//                        inputStates.getCurrent().getMouseState().getY());
//                    final Ray3 pickRay = new Ray3();
//                    _canvas.getCanvasRenderer().getCamera().getPickRay(pos, false, pickRay);
//                    Vector2.releaseTempInstance(pos);
//                    doPick(pickRay);
//                }
//            }));
//
//        final Predicate<TwoInputStates> clickLeftOrRight = Predicates.or(new MouseButtonClickedCondition(
//            MouseButton.LEFT), new MouseButtonClickedCondition(MouseButton.RIGHT));
//
//        _logicalLayer.registerTrigger(new InputTrigger(clickLeftOrRight, new TriggerAction() {
//
//            public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
//                System.err.println("clicked: " + inputStates.getCurrent().getMouseState().getClickCounts());
//            }
//        }));


        getLogicalLayer().registerTrigger(new InputTrigger(new MouseButtonPressedCondition(MouseButton.LEFT),
            new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                    leftPressed();
                }
            }));

        getLogicalLayer().registerTrigger(new InputTrigger(new MouseButtonReleasedCondition(MouseButton.LEFT),
            new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                    leftReleased();
                }
            }));

        getLogicalLayer().registerTrigger(new InputTrigger(new MouseMovedCondition(), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                pixelPos.set(inputStates.getCurrent().getMouseState().getX(), inputStates.getCurrent().getMouseState().getY());

                moved();
            }
        }));

    }

    public Node getRoot() {
        return spacetime.getRoot();
    }

    public NativeCanvas getCanvas() {
        return spacetime.getCanvas();
    }

    public LogicalLayer getLogicalLayer() {
        return spacetime.getLogicalLayer();
    }

    public MouseManager getMouseManager() {
        return spacetime.getMouseManager();
    }

    protected void processPicks(final PrimitivePickResults pickResults) {

//        for (int i = 0; i < pickResults.getNumber(); i++) {
//            PickData pd = pickResults.getPickData(i);
//        }

        int i = 0;
        while (pickResults.getNumber() > 0 && pickResults.getPickData(i).getIntersectionRecord().getNumberOfIntersection() == 0 && ++i < pickResults.getNumber()) {
        }
        if (pickResults.getNumber() > i) {
            final PickData pick = pickResults.getPickData(i);

            final Tangible topLevel = getTangible(pick.getTargetMesh());
            setTangible(topLevel, pick);

            if (topLevel instanceof Touchable) {
                setPicked((Touchable) topLevel, pick);
            } else {
                setPicked(null, null);
            }

        } else {
            setTangible(null, null);
            setPicked(null, null);
            //System.err.println("picked: nothing");
        }
    }

    private void setTangible(Tangible t, PickData pick) {
        this.currentTangible = t;
        this.tangiblePick = pick;
    }

    private void setPicked(Touchable p, PickData pick) {

        if (this.currentlyPicked == p) {
            if (p != null) {
                currentlyPicked.touching(pick);
            }
        } else {

            if (this.currentlyPicked != null) {
                currentlyPicked.touchStop();
            }

            this.currentlyPicked = p;

            if (currentlyPicked != null) {
                p.touchStart(pick);
            }
        }
    }

    public static Tangible getTangible(final Spatial target) {
        if (target instanceof Tangible) {
            if (((Tangible) target).isTangible()) {
                return ((Tangible) target);
            }
        }
        if (target.getParent() == null) {
            return null;
        } else {
            return getTangible(target.getParent());
        }
    }

    public PickResults doPick(Ray3 pickRay) {
        pickResults.clear();

        pickResults.setCheckDistance(true);
        PickingUtil.findPick(getRoot(), pickRay, pickResults);

        processPicks(pickResults);

        return pickResults;
    }
}
