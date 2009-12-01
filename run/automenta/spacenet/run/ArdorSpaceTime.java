/**
 * Copyright (c) 2008-2009 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */
package automenta.spacenet.run;

import automenta.spacenet.space.*;
import automenta.spacenet.space.control.Pickable;
import automenta.spacenet.space.control.Pressable;
import automenta.spacenet.space.control.Tangible;
import com.ardor3d.example.*;
import java.awt.EventQueue;
import java.awt.event.ComponentEvent;
import java.net.URL;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.ArdorModule;
import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.framework.Scene;
import com.ardor3d.framework.Updater;
import com.ardor3d.framework.jogl.JoglCanvas;
import com.ardor3d.image.Image.Format;
import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.image.util.ScreenShotImageExporter;
import com.ardor3d.input.ControllerWrapper;
import com.ardor3d.input.FocusWrapper;
import com.ardor3d.input.GrabbedState;
import com.ardor3d.input.Key;
import com.ardor3d.input.KeyboardWrapper;
import com.ardor3d.input.MouseButton;
import com.ardor3d.input.MouseManager;
import com.ardor3d.input.MouseWrapper;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.control.FirstPersonControl;
import com.ardor3d.input.logical.AnyKeyCondition;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyPressedCondition;
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
import com.ardor3d.light.PointLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.TextureRendererFactory;
import com.ardor3d.renderer.jogl.JoglTextureRendererProvider;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.event.DirtyType;
import com.ardor3d.util.Constants;
import com.ardor3d.util.ContextGarbageCollector;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.GameTaskQueueManager;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.geom.Debugger;
import com.ardor3d.util.screen.ScreenExporter;
import com.ardor3d.util.stat.StatCollector;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.Stage;
import java.awt.event.ComponentAdapter;

public abstract class ArdorSpaceTime implements Runnable, Updater, Scene, Exit {

    private static final Logger logger = Logger.getLogger(ArdorSpaceTime.class.getName());
    protected final LogicalLayer _logicalLayer;
    protected PhysicalLayer _physicalLayer;
    protected final Space _root = new Space();
    protected final FrameHandler _frameHandler;
    protected LightState _lightState;
    protected WireframeState _wireframeState;
    protected volatile boolean _exit = false;
    protected static boolean _stereo = false;
    protected boolean _showBounds = false;
    protected boolean _showNormals = false;
    protected boolean _showDepth = false;
    protected boolean _doShot = false;
    protected NativeCanvas _canvas;
    protected ScreenShotImageExporter _screenShotExp = new ScreenShotImageExporter();
    protected MouseManager _mouseManager;
    protected FirstPersonControl _controlHandle;
    protected Vector3 _worldUp = new Vector3(0, 1, 0);
    protected static int _minDepthBits = -1;
    protected static int _minAlphaBits = -1;
    protected static int _minStencilBits = -1;
    private PrimitivePickResults pickResults;
    private Pickable currentlyPicked;
    private Tangible currentTangible;
    private Pressable currentlyPressed;
    private PickData tangiblePick;

    @Inject
    public ArdorSpaceTime(final LogicalLayer logicalLayer, final FrameHandler frameHandler) {
        _logicalLayer = logicalLayer;
        _frameHandler = frameHandler;

    }

    public MouseManager getMouseManager() {
        return _mouseManager;
    }

    public void run() {
        try {
            _frameHandler.init();


            while (!_exit) {
                _frameHandler.updateFrame();
                Thread.yield();
            }
            // grab the graphics context so cleanup will work out.
            _canvas.getCanvasRenderer().setCurrentContext();
            quit(_canvas.getCanvasRenderer().getRenderer());
        } catch (final Throwable t) {
            System.err.println("Throwable caught in MainThread - exiting");
            t.printStackTrace(System.err);
        }
    }

    public void exit() {
        _exit = true;
    }

    @MainThread
    public void init() {

        initInput();

        AWTImageLoader.registerLoader();

//        try {
//            SimpleResourceLocator srl = new SimpleResourceLocator(ArdorSpaceTime.class.getClassLoader().getResource(
//                    "com/ardor3d/example/media/"));
//            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, srl);
//            srl = new SimpleResourceLocator(ArdorSpaceTime.class.getClassLoader().getResource(
//                    "com/ardor3d/example/media/models/"));
//            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_MODEL, srl);
//        } catch (final URISyntaxException ex) {
//            ex.printStackTrace();
//        }

        /**
         * Create a ZBuffer to display pixels closest to the camera above farther ones.
         */
        final ZBufferState buf = new ZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        _root.setRenderState(buf);

        // ---- LIGHTS
        /** Set up a basic, default light. */
        final PointLight light = new PointLight();
        light.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));
        light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        light.setLocation(new Vector3(100, 100, 100));
        light.setEnabled(true);

        /** Attach the light to a lightState and the lightState to rootNode. */
        _lightState = new LightState();
        _lightState.setEnabled(true);
        _lightState.attach(light);
        _root.setRenderState(_lightState);

        _wireframeState = new WireframeState();
        _wireframeState.setEnabled(false);
        _root.setRenderState(_wireframeState);

        _root.getSceneHints().setRenderBucketType(RenderBucketType.Opaque);

        if (_canvas instanceof JoglCanvas) {
            ((JoglCanvas) _canvas).addComponentListener(new ComponentAdapter() {

                @Override public void componentResized(ComponentEvent e) {
                    java.awt.Dimension s = e.getComponent().getSize();
                    _canvas.getCanvasRenderer().getCamera().resize(s.width, s.height);
                }
            });
        }

        // Set up a reusable pick results
        pickResults = new PrimitivePickResults();
        pickResults.setCheckDistance(true);

        initWindow();
    }

    protected abstract void initWindow();

    @MainThread
    public void update(final ReadOnlyTimer timer) {
        if (_canvas.isClosing()) {
            //needs to be first in this method
            exit();
        }




        /** update stats, if enabled. */
        if (Constants.stats) {
            StatCollector.update();
        }

        updateLogicalLayer(timer);

        // Execute updateQueue item
        GameTaskQueueManager.getManager(_canvas.getCanvasRenderer().getRenderContext()).getQueue(GameTaskQueue.UPDATE).execute();

        /** Call simpleUpdate in any derived classes of ExampleBase. */
        updateWindow(timer);



        /** Update controllers/render states/transforms/bounds for rootNode. */
        _root.updateGeometricState(timer.getTimePerFrame(), true);
    }

    protected void updateLogicalLayer(final ReadOnlyTimer timer) {
        // check and execute any input triggers, if we are concerned with input
        if (_logicalLayer != null) {
            _logicalLayer.checkTriggers(timer.getTimePerFrame());
        }
    }

    protected void updateWindow(final ReadOnlyTimer timer) {
        // does nothing
    }

    @MainThread
    public boolean renderUnto(final Renderer renderer) {
        // Execute renderQueue item
        GameTaskQueueManager.getManager(_canvas.getCanvasRenderer().getRenderContext()).getQueue(GameTaskQueue.RENDER).execute(renderer);

        // Clean up card garbage such as textures, vbos, etc.
        ContextGarbageCollector.doRuntimeCleanup(renderer);

        /** Draw the rootNode and all its children. */
        if (!_canvas.isClosing()) {
            /** Call renderExample in any derived classes. */
            renderExample(renderer);
            renderDebug(renderer);

            if (_doShot) {
                // force any waiting scene elements to be renderer.
                renderer.renderBuckets();
                ScreenExporter.exportCurrentScreen(_canvas.getCanvasRenderer().getRenderer(), _screenShotExp);
                _doShot = false;
            }
            return true;
        } else {
            return false;
        }
    }

    protected void renderExample(final Renderer renderer) {
        renderer.draw(_root);
    }

    protected void renderDebug(final Renderer renderer) {
        if (_showBounds) {
            Debugger.drawBounds(_root, renderer, true);
        }

        if (_showNormals) {
            Debugger.drawNormals(_root, renderer);
            Debugger.drawTangents(_root, renderer);
        }

        if (_showDepth) {
            renderer.renderBuckets();
            Debugger.drawBuffer(Format.Depth16, Debugger.NORTHEAST, renderer);
        }
    }

    public PickResults doPick(final Ray3 pickRay) {

        pickResults.clear();

        pickResults.setCheckDistance(true);
        PickingUtil.findPick(getRoot(), pickRay, pickResults);

        processPicks(pickResults);

        return pickResults;
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

            if (topLevel instanceof Pickable) {
                setPicked((Pickable) topLevel, pick);
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

    private void setPicked(Pickable p, PickData pick) {

        if (this.currentlyPicked == p) {
            if (p != null) {
                currentlyPicked.pick(pick);
            }
        } else {

            if (this.currentlyPicked != null) {
                currentlyPicked.pickStop();
            }

            this.currentlyPicked = p;

            if (currentlyPicked != null) {
                p.pickStart(pick);
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

    protected void quit(final Renderer renderer) {
        ContextGarbageCollector.doFinalCleanup(renderer);
        _canvas.close();
    }

    public static void newWindow(final Class<? extends ArdorSpaceTime> exampleClazz) {
        // Ask for properties
        final PropertiesGameSettings prefs = getAttributes(new PropertiesGameSettings("ardorSettings.properties", null));

        // Convert to DisplayProperties (XXX: maybe merge these classes?)
        final DisplaySettings settings = new DisplaySettings(prefs.getWidth(), prefs.getHeight(), prefs.getDepth(),
            prefs.getFrequency(),
            // alpha
            _minAlphaBits != -1 ? _minAlphaBits : prefs.getAlphaBits(),
            // depth
            _minDepthBits != -1 ? _minDepthBits : prefs.getDepthBits(),
            // stencil
            _minStencilBits != -1 ? _minStencilBits : prefs.getStencilBits(),
            // samples
            prefs.getSamples(),
            // other
            prefs.isFullscreen(), _stereo);

        // get our framework
        final ArdorModule ardorModule = new ArdorModule();
        Module systemModule = null;

        if ("LWJGL".equalsIgnoreCase(prefs.getRenderer())) {
//            systemModule = new LwjglModule();
//            TextureRendererFactory.INSTANCE.setProvider(new LwjglTextureRendererProvider());
        } else if ("JOGL".equalsIgnoreCase(prefs.getRenderer())) {
            systemModule = new JoglModule();
            TextureRendererFactory.INSTANCE.setProvider(new JoglTextureRendererProvider());
        }
        final Module exampleModule = new AbstractModule() {

            @Override
            protected void configure() {
                bind(ArdorSpaceTime.class).to(exampleClazz).in(Scopes.SINGLETON);
                bind(Scene.class).to(ArdorSpaceTime.class);
                bind(Updater.class).to(ArdorSpaceTime.class);
                bind(Exit.class).to(ArdorSpaceTime.class);
            }
        };
        final Provider<DisplaySettings> settingsProvider = new Provider<DisplaySettings>() {

            public DisplaySettings get() {
                return settings;
            }
        };

        // Setup our injector.
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, ardorModule, systemModule, exampleModule,
            new AbstractModule() {

                @Override
                protected void configure() {
                    bind(DisplaySettings.class).toProvider(settingsProvider);
                }
            });

        final LogicalLayer ll = injector.getInstance(LogicalLayer.class);
        final FrameHandler frameWork = injector.getInstance(FrameHandler.class);
        final ArdorSpaceTime gameRunnable = injector.getInstance(ArdorSpaceTime.class);
        final NativeCanvas canvas = injector.getInstance(NativeCanvas.class);
        final Updater updater = injector.getInstance(Updater.class);
        final PhysicalLayer physicalLayer = new PhysicalLayer(injector.getInstance(KeyboardWrapper.class), injector.getInstance(MouseWrapper.class), injector.getInstance(ControllerWrapper.class), injector.getInstance(FocusWrapper.class));

        // set the mouse manager member. It's a bit of a hack to do that this way.
        gameRunnable._mouseManager = injector.getInstance(MouseManager.class);

        ll.registerInput(canvas, physicalLayer);

        // Register our example as an updater.
        frameWork.addUpdater(updater);

        // Make a native canvas and register it.
        frameWork.addCanvas(canvas);

        gameRunnable._canvas = canvas;
        gameRunnable._physicalLayer = physicalLayer;

        new Thread(gameRunnable).start();
    }

    protected static PropertiesGameSettings getAttributes(final PropertiesGameSettings settings) {
        // Always show the dialog in these examples.
        URL dialogImage = null;
        final String dflt = settings.getDefaultSettingsWidgetImage();
        if (dflt != null) {
            try {
                dialogImage = ArdorSpaceTime.class.getResource(dflt);
            } catch (final Exception e) {
                logger.log(Level.SEVERE, "Resource lookup of '" + dflt + "' failed.  Proceeding.");
            }
        }
        if (dialogImage == null) {
            logger.fine("No dialog image loaded");
        } else {
            logger.fine("Using dialog image '" + dialogImage + "'");
        }

        final URL dialogImageRef = dialogImage;
        final AtomicReference<PropertiesDialog> dialogRef = new AtomicReference<PropertiesDialog>();
        final Stack<Runnable> mainThreadTasks = new Stack<Runnable>();
        try {
            if (EventQueue.isDispatchThread()) {
                dialogRef.set(new PropertiesDialog(settings, dialogImageRef, mainThreadTasks));
            } else {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        dialogRef.set(new PropertiesDialog(settings, dialogImageRef, mainThreadTasks));
                    }
                });
            }
        } catch (final Exception e) {
            logger.logp(Level.SEVERE, ArdorSpaceTime.class.getClass().toString(), "ExampleBase.getAttributes(settings)",
                "Exception", e);
            return null;
        }

        PropertiesDialog dialogCheck = dialogRef.get();
        while (dialogCheck == null || dialogCheck.isVisible()) {
            try {
                // check worker queue for work
                while (!mainThreadTasks.isEmpty()) {
                    mainThreadTasks.pop().run();
                }
                // go back to sleep for a while
                Thread.sleep(50);
            } catch (final InterruptedException e) {
                logger.warning("Error waiting for dialog system, using defaults.");
            }

            dialogCheck = dialogRef.get();
        }

        if (dialogCheck.isCancelled()) {
            System.exit(0);
        }
        return settings;
    }

    public Space getRoot() {
        return _root;
    }

    protected void initInput() {

        // check if this example worries about input at all
        if (_logicalLayer == null) {
            return;
        }

        _controlHandle = FirstPersonControl.setupTriggers(_logicalLayer, _worldUp, true);


        _logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.ESCAPE), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                exit();
            }
        }));

        _logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.L), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                _lightState.setEnabled(!_lightState.isEnabled());
                // Either an update or a markDirty is needed here since we did not touch the affected spatial directly.
                _root.markDirty(DirtyType.RenderState);
            }
        }));

        _logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.F4), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                _showDepth = !_showDepth;
            }
        }));

        _logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.T), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                _wireframeState.setEnabled(!_wireframeState.isEnabled());
                // Either an update or a markDirty is needed here since we did not touch the affected spatial directly.
                _root.markDirty(DirtyType.RenderState);
            }
        }));

        _logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.B), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                _showBounds = !_showBounds;
            }
        }));

        _logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.N), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                _showNormals = !_showNormals;
            }
        }));

        _logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.F1), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                _doShot = true;
            }
        }));



        _logicalLayer.registerTrigger(new InputTrigger(new AnyKeyCondition(), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                System.out.println("Key character pressed: " + inputState.getCurrent().getKeyboardState().getKeyEvent().getKeyChar());
            }
        }));


        initPointer();
    }

    protected void initPointer() {
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

        _logicalLayer.registerTrigger(new InputTrigger(new MouseButtonPressedCondition(MouseButton.LEFT),
            new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                    if (_mouseManager.isSetGrabbedSupported()) {
                        _mouseManager.setGrabbed(GrabbedState.GRABBED);
                    }

                    if (currentTangible instanceof Pressable) {
                        currentlyPressed = (Pressable)currentTangible;
                        ((Pressable) currentTangible).pressStart(tangiblePick);
                    }
                }
            }));

        _logicalLayer.registerTrigger(new InputTrigger(new MouseButtonReleasedCondition(MouseButton.LEFT),
            new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                    if (_mouseManager.isSetGrabbedSupported()) {
                        _mouseManager.setGrabbed(GrabbedState.NOT_GRABBED);
                    }

                    if (currentlyPressed != null) {
                        currentlyPressed.pressStop(tangiblePick);
                        currentlyPressed = null;
                    }
                }
            }));

        _logicalLayer.registerTrigger(new InputTrigger(new MouseMovedCondition(), new TriggerAction() {

            Vector2 pos = new Vector2();
            final Ray3 pickRay = new Ray3();

            public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                pos.set(inputStates.getCurrent().getMouseState().getX(), inputStates.getCurrent().getMouseState().getY());
                _canvas.getCanvasRenderer().getCamera().getPickRay(pos, false, pickRay);

                doPick(pickRay);
            }
        }));
    }
}
