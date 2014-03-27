/*
 The MIT License (MIT)

 Copyright (c) 2014 Tushar Joshi

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package com.tusharjoshi.runargs;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DynamicMenuContent;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;

/**
 *
 * @author Tushar Joshi
 */
@ActionID(
        category = "Build",
        id = "com.tusharjoshi.runargs.DebugProjectAction"
)
@ActionRegistration(displayName = "#CTL_DebugProjectAction", lazy = false)
@ActionReferences({
    @ActionReference(path = "Menu/Debug", position = 0),
    @ActionReference(path = "Projects/Actions", position = 10),    
    @ActionReference(path = "Shortcuts", name = "D-S-D")
})
@NbBundle.Messages({
    "CTL_DebugProjectAction=Debug with Arguments"})
public class DebugProjectAction extends AbstractAction 
implements ContextAwareAction {   

    private Project project;
    
    private Lookup.Result<Project> result;
    
    private Lookup lkp;
    
    private final LookupListener listener = new DebugLookupListener();

    @Override
    public Action createContextAwareInstance(Lookup lkp) {
        return new DebugProjectAction(lkp);
    }
    
    public DebugProjectAction() {
        this(Utilities.actionsGlobalContext());
    }

    public DebugProjectAction(final Lookup lkp) {

        this.lkp = lkp;        
        this.result = lkp.lookupResult(Project.class);
        this.result.addLookupListener(
                WeakListeners.create(LookupListener.class, listener, this.result));
        
        putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);        
        putValue(ACCELERATOR_KEY, Utilities.stringToKey("D-S-D"));

        lookupChanged();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        
        new AntCommandHandler().debugProject(project);
    }
    
    private class DebugLookupListener implements LookupListener {

        @Override
        public void resultChanged(LookupEvent ev) {
            lookupChanged();
        }
        
    }

    public final void lookupChanged() {
        project = AntCommandHandler.findProject( lkp);
        
        String projectName = "";
        boolean enableMenu = false;
        
        if( null != project && Constants.J2SEPROJECT
                .equals(project.getClass().getName()) ) {
            projectName = AntCommandHandler.getProjectName(project);
            enableMenu = true;
        }
            
        putValue(NAME, Bundle.MSG_INPUT_TITLE(projectName, 
                Constants.COMMAND_DEBUG_NAME));
        //setEnabled(enableMenu);
        setEnabled(true);
    }    
}
