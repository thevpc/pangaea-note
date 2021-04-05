/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.function.Supplier;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;

/**
 *
 * @author vpc
 */
public class PangaeaNoteAppExtensionHandlerImpl implements PangaeaNoteAppExtensionHandler {

    private PangaeaNoteGuiApp sapp;

    private Supplier<PangaeaNoteAppExtension> extensionLoader;
    private PangaeaNoteAppExtension extension;
    private PangaeaNoteAppExtensionStatus state = PangaeaNoteAppExtensionStatus.UNLOADED;
    private Exception error;
    private boolean disabled;
    private boolean loaded;
    private PropertyChangeSupport pcs;

    public PangaeaNoteAppExtensionHandlerImpl(PangaeaNoteGuiApp sapp, Supplier<PangaeaNoteAppExtension> extensionSupplier) {
        this.sapp = sapp;
        this.extensionLoader = extensionSupplier;
        pcs = new PropertyChangeSupport(this);
    }

    @Override
    public PangaeaNoteAppExtensionStatus getState() {
        if (disabled) {
            return PangaeaNoteAppExtensionStatus.DISABLED;
        }
        return state;
    }

    @Override
    public void setDisabled(boolean b) {
        if (this.disabled != b) {
            this.disabled = b;
            if (loaded) {
                if (disabled) {
                    extension.onDisable(sapp);
                } else {
                    extension.onEnable(sapp);
                }
            }
        }
    }

    @Override
    public boolean checkLoaded() {
        if (this.state == state.LOADED) {
            return true;
        }
        if (this.state == state.UNLOADED) {
            state = PangaeaNoteAppExtensionStatus.LOADING;
            fireStateChange(state.UNLOADED, PangaeaNoteAppExtensionStatus.LOADING);
            try {
                PangaeaNoteAppExtension a = extensionLoader.get();
                if (a != null) {
                    extension = a;
                    state = PangaeaNoteAppExtensionStatus.LOADED;
                    a.onLoad(sapp);
                    fireStateChange(PangaeaNoteAppExtensionStatus.LOADING, PangaeaNoteAppExtensionStatus.LOADED);
                    return true;
                } else {
                    error = new IllegalArgumentException("Null Extension");
                    state = PangaeaNoteAppExtensionStatus.ERROR;
                    fireStateChange(PangaeaNoteAppExtensionStatus.LOADING, PangaeaNoteAppExtensionStatus.ERROR);
                }
            } catch (Exception ex) {
                extension = null;
                error = ex;
                state = PangaeaNoteAppExtensionStatus.ERROR;
                fireStateChange(PangaeaNoteAppExtensionStatus.LOADING, PangaeaNoteAppExtensionStatus.ERROR);
            }
        }
        return false;
    }

    public PangaeaNoteAppExtension getExtension() {
        switch (state) {
            case UNLOADED: {
                throw new IllegalStateException("Extension is unloaded");
            }
            case LOADED: {
                return extension;
            }
            case DISABLED: {
                throw new IllegalStateException("Extension is disabled");
            }
            case ERROR: {
                throw new IllegalStateException("Extension cloudnt be loaded");
            }
            case LOADING: {
                throw new IllegalStateException("Extension is still loading");
            }
        }
        throw new IllegalStateException("Extension with unsupported state");
    }

    private void fireAdded() {

    }

    public void addListener(String prop, PropertyChangeListener li) {
        pcs.addPropertyChangeListener(prop, li);
    }

    private void fireStateChange(PangaeaNoteAppExtensionStatus from, PangaeaNoteAppExtensionStatus to) {
        pcs.firePropertyChange("status", from, to);
    }

}
