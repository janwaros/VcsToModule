package com.github.janwaros.VcsToModule;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.vcs.checkout.NewProjectCheckoutListener;
import com.intellij.openapi.vcs.checkout.VcsAwareCheckoutListener;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 07.09.2013
 * Time: 01:44
 */
public class DefaultExtensionUnregisterer implements ApplicationComponent {
    public DefaultExtensionUnregisterer() {
    }

    public void initComponent() {
        VcsAwareCheckoutListener defaultExtension = VcsAwareCheckoutListener.EP_NAME.findExtension(NewProjectCheckoutListener.class);
        if(defaultExtension != null) {
            Extensions.getRootArea().getExtensionPoint(VcsAwareCheckoutListener.EP_NAME).unregisterExtension(defaultExtension);
        }

    }

    public void disposeComponent() {}

    @NotNull
    public String getComponentName() {
        return "com.github.janwaros.VcsToModule.plugins.DefaultExtensionUnregistrer";
    }
}
