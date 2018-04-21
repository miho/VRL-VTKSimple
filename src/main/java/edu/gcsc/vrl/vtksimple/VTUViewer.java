package edu.gcsc.vrl.vtksimple;


import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.reflection.DefaultMethodRepresentation;
import eu.mihosoft.vrl.reflection.VisualCanvas;
import eu.mihosoft.vrl.system.VRL;
import eu.mihosoft.vrl.types.MethodRequest;
import eu.mihosoft.vrl.types.VGeometry3DType;
import eu.mihosoft.vrl.v3d.VGeometry3D;
import eu.mihosoft.vrl.visual.VSwingUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@ComponentInfo(name = "VTU-Viewer", category = "VTK")
public class VTUViewer implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient GridPainter3D painter;
    private transient StoppableThread thread;
    private transient AtomicBoolean invokedFromThread;
    private transient File fileToPlot;

    private GridPainter3D getPainter() {
        if (painter == null) {
            painter = new GridPainter3D();
        }

        return painter;
    }

    private AtomicBoolean getInvokedFromThread() {
        if (invokedFromThread == null) {
            invokedFromThread = new AtomicBoolean(false);
        }

        return invokedFromThread;
    }

    private void stopThread() {
        if (thread != null) {
            thread.stopThread();
            thread = null;
        }
    }

    @MethodInfo(hide = false, valueOptions = "serialization=false;doEmpty=false")
    public VGeometry3D view(MethodRequest mReq,
                            @ParamInfo(
                                    name = "VTU-File(s)",
                                    style = "load-dialog",
                                    options = "endings=[\".vtu\"]; description=\"*.vtu - Files\"")
                                    File fileOrFolder,
                            @ParamInfo(name = "Data Array", options = "value=\"c\"") String dataArray) {

        DefaultMethodRepresentation mRep = mReq.getMethod();

        if(thread!=null && thread.isAlive()) {
            //mRep.changeInvokeButtonTextIfButtonIsPresent("")
        }

        stopThread();
        thread = registerListener(fileOrFolder, (f) -> {
            AtomicBoolean success = new AtomicBoolean(false);
            int counter = 0;
            while(!success.get() && counter < 10) {
                counter++;
                VSwingUtil.invokeAndWait(() -> {
                    fileToPlot = f;
                    getInvokedFromThread().set(true);
                    VGeometry3D value = null;
                    if (fileToPlot != null && fileToPlot.exists()) {
                        try {
                            value = getPainter().paint(Color.GREEN, Color.RED, fileToPlot, dataArray, false);
                        } catch (Exception ex) {
                            // we continue the visualization, even if errors occur
                            success.set(false);
                        }
                    }
                    if (value != null) {
                        mRep.getReturnValue().setViewValue(value);
                        mRep.getParentObject().getParentWindow().setTitle("VTU-Viewer: " + fileToPlot.getName());
                        success.set(true);
                    }
                });
            }
        });
        VRL.getCurrentProjectController().addSessionThread(thread);

        if(!fileOrFolder.isDirectory() && fileOrFolder.exists()) {
            fileToPlot = fileOrFolder;
            VGeometry3D value = getPainter().paint(Color.GREEN, Color.RED, fileToPlot, dataArray, false);
            mRep.getReturnValue().setValue(value);
        }

        mRep.getParentObject().getParentWindow().setTitle("VTU-Viewer"+(fileOrFolder.exists()?"":": file does not exist!"));

        return null;
    }

    @MethodInfo(noGUI = true)
    public static void main(String[] args) {

        registerListener(new File("/Users/miho/Documents/tmp/"), (f) -> {
            System.out.println("here: " + f.getAbsolutePath());
        });
    }

    private static class StoppableThread extends Thread {
        private boolean stopLoop = false;

        public void stopThread() {
            stopLoop = true;
        }

        boolean isStopLoop() {
            return stopLoop;
        }
    }

    protected static StoppableThread registerListener(File fileOrFolder, Consumer<File> consumer) {

        StoppableThread t = new StoppableThread() {
            @Override
            public void run() {
                try (WatchService watchService
                             = FileSystems.getDefault().newWatchService()) {

                    Path path;

                    if(fileOrFolder.isDirectory()) {
                        path = fileOrFolder.toPath();
                    }else {
                        path = fileOrFolder.getAbsoluteFile().getParentFile().toPath();
                    }

                    path.register(
                            watchService,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.ENTRY_MODIFY
                    );

                    WatchKey key;
                    while ((key = watchService.take()) != null && !this.isStopLoop()) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            if (event.context() instanceof Path) {
                                File changedFile = ((Path) event.context()).toFile();
                                consumer.accept(new File(path.toFile(), changedFile.getName()));
                            }
                        }
                        key.reset();
                    }

                } catch (IOException | InterruptedException ex) {
                    throw new RuntimeException("File system monitor not working anymore: ", ex);
                }
            }
        };

        t.start();

        return t;
    }
}



