package com.comarch.koscinski.jaroslaw.plugins;

import com.intellij.ide.actions.ImportModuleAction;
import com.intellij.ide.util.newProjectWizard.AddModuleWizard;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ui.configuration.DefaultModulesProvider;
import com.intellij.openapi.roots.ui.configuration.actions.NewModuleAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsBundle;
import com.intellij.openapi.vcs.VcsDirectoryMapping;
import com.intellij.openapi.vcs.VcsKey;
import com.intellij.openapi.vcs.checkout.ProjectCheckoutListener;
import com.intellij.openapi.vcs.checkout.VcsAwareCheckoutListener;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportProvider;
import com.intellij.util.PlatformUtils;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jaroslaw Koscinski
 * Date: 07.09.2013
 * Time: 00:05
 */
public class VcsToModule implements VcsAwareCheckoutListener {

    @Override
    public boolean processCheckedOutDirectory(Project project, File directory, VcsKey vcsKey) {

        int rc = Messages.showDialog(project, "Would you like to create "+getProductNameWithArticle()+" project or module for the sources you have checked out to "+directory.getAbsolutePath()+"?",
                VcsBundle.message("checkout.title"),new String[] {"Create project", "Create module", "Cancel"},0,Messages.getQuestionIcon());

        if(rc ==2 ) return false;

        final ProjectManager pm = ProjectManager.getInstance();
        final Project[] projects = pm.getOpenProjects();
        final Set<VirtualFile> files = projectsLocationSet(projects);
        VirtualFile file = LocalFileSystem.getInstance().findFileByIoFile(directory);
        AddModuleWizard wizard;

        if (rc == 0) {
            wizard = ImportModuleAction.createImportWizard(null, null, file, ProjectImportProvider.PROJECT_IMPORT_PROVIDER.getExtensions());
            if (wizard == null) return false;
            if (wizard.showAndGet()) {
                ImportModuleAction.createFromWizard(null, wizard);
            }
        } else if(rc == 1) {
            wizard = createModuleWizard(project, file);
            if (wizard == null) return false;
            if (wizard.showAndGet()) {
                new NewModuleAction().createModuleFromWizard(project,null,wizard);
            }
        }


        final Project[] projectsAfter = pm.getOpenProjects();

        for (Project project1 : projectsAfter) {
            if (project1.getBaseDir() != null && ! files.contains(project1.getBaseDir())) {
                final ProjectLevelVcsManager vcsManager = ProjectLevelVcsManager.getInstance(project1);
                vcsManager.setDirectoryMappings(Collections.singletonList(new VcsDirectoryMapping("", vcsKey.getName())));
                break;
            }
        }
        return true;
    }

    static String getProductNameWithArticle() {
        final ApplicationNamesInfo namesInfo = ApplicationNamesInfo.getInstance();
        // example: "to create an IntelliJ IDEA project" (full product name is ok);
        // "to create a JetBrains Astella project" (better use not full product name: "to create an Astella project")
        final String productName = PlatformUtils.isIdea() ? namesInfo.getFullProductName() : namesInfo.getProductName();
        final String article = StringUtil.isVowel(Character.toLowerCase(productName.charAt(0))) ? "an " : "a ";
        return article + productName;
    }

    private static Set<VirtualFile> projectsLocationSet(Project[] projects) {
        final Set<VirtualFile> files = new HashSet<VirtualFile>();
        for (Project project1 : projects) {
            if (project1.getBaseDir() != null) {
                files.add(project1.getBaseDir());
            }
        }
        return files;
    }

    public static AddModuleWizard createModuleWizard(final Project project,final VirtualFile file) {
        String path = ProjectImportProvider.getDefaultPath(file);
        return new AddModuleWizard(project, new DefaultModulesProvider(project), path);
    }


}
