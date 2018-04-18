/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.webui.jsf.example.index;        

import com.sun.webui.jsf.example.index.AppData;

/**
 * This class serves as a data wrapper for the Table component in the
 * Example Application Index page.
 */
public class TableData {  
   
    // Fill data for each example in an array object.            
    protected static final AppData[] data = {
        new AppData("index_buttonName","index_buttonConcepts", "showButton",
                new String[] {
                    "button/Button.jsf",
                    "button/ButtonResults.jsf",
                    "button/ButtonBackingBean.java"}
        ),
        new AppData("index_cbrbName","index_cbrbConcepts", "showCheckboxRadiobutton",
                new String[] {
                    "cbrb/checkboxRadiobutton.jsf",
                    "cbrb/checkboxRadiobuttonResults.jsf",
                    "cbrb/CheckboxRadiobuttonBackingBean.java"}
        ),
        new AppData("index_labelName","index_labelConcepts", "showLabel",
                new String[] {
                    "label/Label.jsf",
                    "label/LabelResults.jsf",
                    "label/Help.jsf",
                    "label/LabelBackingBean.java"}
        ),
        new AppData("index_alertName","index_alertConcepts", "showAlertIndex",
                new String[] {
                    "alert/Alert.jsf",
                    "alert/InlineAlert.jsf",
                    "alert/PageAlertExample.jsf",
                    "alert/PageAlert.jsf",
                    "alert/HelpAlert.jsf",
                    "alert/InlineAlertBackingBean.java",        
                    "alert/PageAlertBackingBean.java"}
        ),
        new AppData("index_textInputName", "index_textInputConcepts", "showTextInput", 
                new String[] {
                    "field/TextInput.jsf",
                    "field/TextInputResults.jsf",
                    "field/TextInputBackingBean.java"}
        ),
        new AppData("index_tableName", "index_tableConcepts", "showTableIndex", 
                new String[] {
                    "table/actions.jsf",
                    "table/actionsBottom.jsf",
                    "table/actionsTop.jsf",
                    "table/alarms.jsf",
                    "table/basicTable.jsf",
                    "table/customTitle.jsf",
                    "table/dynamicGroupTable.jsf",
                    "table/dynamicTable.jsf",
                    "table/embeddedActions.jsf",
                    "table/emptyCells.jsf",
                    "table/filter.jsf",
                    "table/filterPanel.jsf",
                    "table/groupTable.jsf",
                    "table/hiddenRowsActionsBottom.jsf",        
                    "table/hiddenRowsActionsTop.jsf",        
                    "table/hiddenSelectedRows.jsf",                    
                    "table/index.jsf",
                    "table/multipleHeadersFooters.jsf",
                    "table/liteTable.jsf",
                    "table/paginatedTable.jsf",
                    "table/preferences.jsf",
                    "table/preferencesPanel.jsf",
                    "table/selectMultipleRows.jsf",
                    "table/selectSingleRow.jsf",
                    "table/spacerColumn.jsf",
                    "table/sortableTable.jsf",
                    "table/table.jsf",
                    "table/TableBean.java",
                    "table/DynamicGroupTableBean.java",
                    "table/DynamicTableBean.java",
                    "table/util/Actions.java",
                    "table/util/Dynamic.java",
                    "table/util/Filter.java",
                    "table/util/Group.java",
                    "table/util/Messages.java",
                    "table/util/Name.java",
                    "table/util/Preferences.java",
                    "table/util/Select.java",
                    "table/js/actions.js",
                    "table/js/filter.js",
                    "table/js/preferences.js",
                    "table/js/select.js"}
        ),
        new AppData("index_addRemoveName", "index_addRemoveConcepts", "showAddRemove",
                new String[] {
                    "addremove/AddRemove.jsf",
                    "addremove/AddRemoveResults.jsf",
                    "addremove/AddRemoveBackingBean.java",
                    "common/UserData.java"}
        ),
        new AppData("index_orderableListName", "index_orderableListConcepts", "showOrderableList", 
                new String[] {
                    "orderablelist/OrderableList.jsf",
                    "orderablelist/OrderableListResults.jsf",
                    "orderablelist/OrderableListBackingBean.java",
                    "orderablelist/Flavor.java",
                    "common/UserData.java"}        
        ),
        new AppData("index_chooserUploader", "index_chooserUploaderConcepts", "showChooserUploader",
                new String[] {
                    "chooseruploader/fileUploader.jsf",
                    "chooseruploader/fileChooser.jsf", 
                    "chooseruploader/folderChooserPopup.jsf",
                    "chooseruploader/fileUploaderPopup.jsf",
                    "chooseruploader/folderChooser.jsf", 
                    "chooseruploader/fileChooserPopup.jsf",        
                    "chooseruploader/FileChooserBackingBean.java",  
                    "chooseruploader/FolderChooserBackingBean.java",
                    "chooseruploader/FileUploaderBackingBean.java",        
                    "chooseruploader/ChooserUploaderBackingBean.java",
                    "chooseruploader/ChooserUploaderValidator.java"}
        ),
        new AppData("index_menuListName", "index_menuListConcepts", "showMenuList", 
                new String[] {
                    "menu/MenuList.jsf",
                    "menu/MenuListResults.jsf",
                    "menu/MenuListBackingBean.java"}
        ),
        new AppData("index_mastheadName", "index_mastheadConcepts", "showMasthead",
                new String[] {
                    "masthead/Index.jsf",
                    "masthead/Masthead.jsf",
                    "masthead/MastheadFacets.jsf",
                    "masthead/Version.jsf",
                    "masthead/Popup.jsf",
                    "masthead/Images.jsf",
                    "masthead/ResultMasthead.jsf",
                    "masthead/ResultMastheadFacets.jsf",
                    "masthead/MastheadBackingBean.java"}
        ),
        new AppData("index_propertysheet", "index_propertySheetConcepts", "showPropertySheet",
                new String[] {
                    "propertysheet/PropertySheet.jsf",
                    "propertysheet/PropertySheetResult.jsf",         
                    "propertysheet/PropertySheetBackingBean.java"}
        ),
        new AppData("index_editablelist", "index_editableListConcepts", "showEditableList",
                new String[] {
                    "editablelist/editableList.jsf",
                    "editablelist/editableListResult.jsf",        
                    "editablelist/EditableListBackingBean.java"}
        ),
        new AppData("index_pagetitleName", "index_pagetitleConcepts", "showPagetitle",
                new String[] {
                    "pagetitle/Pagetitle.jsf",
                    "pagetitle/PagetitleBackingBean.java"}
        ),
        new AppData("index_hyperlink", "index_hyperlinkConcepts", "showHyperlink",
                new String[] {
                    "hyperlink/hyperLink.jsf",
                    "hyperlink/hyperLinkResult.jsf",        
                    "hyperlink/HyperlinkBackingBean.java"}
        ),
        new AppData("index_statictextName", "index_statictextConcepts", "showStaticText",
                new String[] {
                    "statictext/Statictext.jsf",
                    "statictext/StatictextBackingBean.java",
                    "statictext/Employee.java",
                    "statictext/EmployeeConverter.java"}
        ),         
        new AppData("index_commonTaskName", "index_commonTaskConcepts", "showCommonTask",
                new String[] {
                    "commontask/commonTasks.jsf",
                    "commontask/sample.jsf",
                    "commontask/commonTaskBean.java"}
        ),
        new AppData("index_treeName", "index_treeConcepts", "showTreeIndex",
                new String[] {
                    "tree/content.jsf",
                    "tree/dynamicTree.jsf",
                    "tree/header.jsf",
                    "tree/index.jsf",
                    "tree/navTree.jsf",
                    "tree/treeFrame.jsf",
		    "common/ClientSniffer.java",
		    "util/ExampleUtilities.java",
		    "tree/DynamicTreeBackingBean.java",
		    "tree/NavTreeBackingBean.java"}
        ),
        new AppData("index_progressBar", "index_progressBarConcepts", "showProgressBar",
                new String[] {
                    "progressbar/index.jsf",
                    "progressbar/determinate.jsf",
                    "progressbar/indeterminate.jsf",
                    "progressbar/busy.jsf",
                    "progressbar/ProgressBarBackingBean.java"}
        ),
        new AppData("index_wizardName","index_wizardConcepts", "showWizardIndex",
                new String[] {
                    "wizard/index.jsf",
                    "wizard/simpleWizard.jsf",
                    "wizard/SimpleWizardBackingBean.java"}
        )
    };    
    
    /** Default constructor */
    public TableData() { 		
    }
}
	
