/*
 * ERProcessorView.java
 */
package edu.duke.archives.erprocessor;

import edu.duke.archives.erprocessor.metadata.ComponentQualifiedMetadata;
import edu.duke.archives.erprocessor.metadata.QualifiedMetadata;
import edu.duke.archives.erprocessor.metadata.mets.MetsFile;
import edu.duke.archives.erprocessor.metadata.mets.MetsNode;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TreeSelectionEvent;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 * The application's main frame.
 */
public class ERProcessorView extends FrameView implements TreeSelectionListener,
        ActionListener {

    private boolean saveEnabled = false;
    
    public ERProcessorView(SingleFrameApplication app) {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate =
                resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
        try {
            loadMetadataTabs();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.getFrame(),
                    "Could not create the custom metadata tabs. " +
                    "The default metadata tab will be used.",
                    "Error Creating Metadata Tabs", JOptionPane.ERROR_MESSAGE);
            System.err.println(e.getLocalizedMessage());

            //Default Metadata Tab code
            Element defaultTab = new Element("tab");
            defaultTab.setAttribute("name", "Default");

            Element altTitle = new Element("input");
            altTitle.setAttribute("type", "text_field");
            altTitle.setAttribute("element", "title");
            altTitle.setAttribute("qualifier", "alternate");
            altTitle.setAttribute("label", "Alternate Title");
            defaultTab.addContent(altTitle);

            Element creator = new Element("input");
            creator.setAttribute("type", "text_field");
            creator.setAttribute("element", "creator");
            creator.setAttribute("label", "Creator");
            defaultTab.addContent(creator);

            Element description = new Element("input");
            description.setAttribute("type", "text_field");
            description.setAttribute("element", "description");
            description.setAttribute("label", "Description");
            defaultTab.addContent(description);

            metadataTabs.add(defaultTab.getAttributeValue("name"),
                    new JScrollPane(createMetadataGroups(defaultTab)));
        }
        ToolTipManager.sharedInstance().registerComponent(fileTree);
        ToolTipManager.sharedInstance().registerComponent(seriesTree);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        defaultToolBar = new javax.swing.JToolBar();
        projOpenBtn = new javax.swing.JButton();
        projSaveBtn = new javax.swing.JButton();
        projCloseBtn = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        accnAddBtn = new javax.swing.JButton();
        accnRemoveBtn = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        printBtn = new javax.swing.JButton();
        eadBtn = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        viewFileBtn = new javax.swing.JButton();
        techMetadataPanel = new javax.swing.JPanel();
        tm_nameLbl = new javax.swing.JLabel();
        tm_typLbl = new javax.swing.JLabel();
        tm_lstModLbl = new javax.swing.JLabel();
        tm_hiddenLbl = new javax.swing.JLabel();
        tm_sizeLbl = new javax.swing.JLabel();
        tm_md5Lbl = new javax.swing.JLabel();
        tm_md5 = new javax.swing.JLabel();
        tm_lstMod = new javax.swing.JLabel();
        tm_size = new javax.swing.JLabel();
        tm_hidden = new javax.swing.JLabel();
        tm_name = new javax.swing.JLabel();
        tm_typ = new javax.swing.JLabel();
        verticalScrollPane = new javax.swing.JSplitPane();
        viewerPanel = new javax.swing.JPanel();
        viewerSplitPane = new javax.swing.JSplitPane();
        filePanel = new javax.swing.JPanel();
        fileLbl = new javax.swing.JLabel();
        fileSP = new javax.swing.JScrollPane();
        fileTree = new javax.swing.JTree();
        seriesPanel = new javax.swing.JPanel();
        seriesLbl = new javax.swing.JLabel();
        seriesSP = new javax.swing.JScrollPane();
        seriesTree = new javax.swing.JTree();
        metadataTabs = new javax.swing.JTabbedPane();
        defaultMetadataTab = new javax.swing.JScrollPane();
        defaultMetadata = new javax.swing.JPanel();
        altTitleLbl = new javax.swing.JLabel();
        altTitleEdit = new javax.swing.JTextField();
        creatorEntityLbl = new javax.swing.JLabel();
        creatorEntityEdit = new javax.swing.JTextField();
        creatorUnitLbl = new javax.swing.JLabel();
        creatorUnitEdit = new javax.swing.JTextField();
        seriesLbl1 = new javax.swing.JLabel();
        seriesEdit = new javax.swing.JComboBox();
        docType = new javax.swing.JLabel();
        docTypeEdit = new javax.swing.JComboBox();
        restrictCkBx = new javax.swing.JCheckBox();
        restrictYrsLbl = new javax.swing.JLabel();
        restrictYrsEdit = new javax.swing.JTextField();
        restrictEdit = new javax.swing.JComboBox();
        excludeCkBx = new javax.swing.JCheckBox();
        excludeEdit = new javax.swing.JComboBox();
        descLbl = new javax.swing.JLabel();
        descSP = new javax.swing.JScrollPane();
        descEdit = new javax.swing.JEditorPane();
        rbmsclTab = new javax.swing.JScrollPane();
        uaMetadataTab = new javax.swing.JScrollPane();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        projOpenM = new javax.swing.JMenuItem();
        projNewM = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        projSaveM = new javax.swing.JMenuItem();
        projCloseM = new javax.swing.JMenuItem();
        projPrintM = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitM = new javax.swing.JMenuItem();
        showMenu = new javax.swing.JMenu();
        showSizeM = new javax.swing.JCheckBoxMenuItem();
        showLastModM = new javax.swing.JCheckBoxMenuItem();
        toolsMenu = new javax.swing.JMenu();
        accnAddM = new javax.swing.JMenuItem();
        accnRmM = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JSeparator();
        convertEadM = new javax.swing.JMenuItem();
        previewFileM = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JSeparator();
        settingsM = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        defaultToolBar.setFloatable(false);
        defaultToolBar.setRollover(true);
        defaultToolBar.setToolTipText("");
        defaultToolBar.setName("defaultToolBar"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(edu.duke.archives.erprocessor.ERProcessor.class).getContext().getActionMap(ERProcessorView.class, this);
        projOpenBtn.setAction(actionMap.get("openProject")); // NOI18N
        projOpenBtn.setFocusable(false);
        projOpenBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        projOpenBtn.setName("projOpenBtn"); // NOI18N
        projOpenBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        defaultToolBar.add(projOpenBtn);

        projSaveBtn.setAction(actionMap.get("saveProject")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(edu.duke.archives.erprocessor.ERProcessor.class).getContext().getResourceMap(ERProcessorView.class);
        projSaveBtn.setText(resourceMap.getString("projSaveBtn.text")); // NOI18N
        projSaveBtn.setToolTipText(resourceMap.getString("projSaveBtn.toolTipText")); // NOI18N
        projSaveBtn.setFocusable(false);
        projSaveBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        projSaveBtn.setName("projSaveBtn"); // NOI18N
        projSaveBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        defaultToolBar.add(projSaveBtn);

        projCloseBtn.setAction(actionMap.get("closeProject")); // NOI18N
        projCloseBtn.setText(resourceMap.getString("projCloseBtn.text")); // NOI18N
        projCloseBtn.setFocusable(false);
        projCloseBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        projCloseBtn.setName("projCloseBtn"); // NOI18N
        projCloseBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        defaultToolBar.add(projCloseBtn);

        jSeparator1.setName("jSeparator1"); // NOI18N
        defaultToolBar.add(jSeparator1);

        accnAddBtn.setAction(actionMap.get("addAccession")); // NOI18N
        accnAddBtn.setText(resourceMap.getString("accnAddBtn.text")); // NOI18N
        accnAddBtn.setFocusable(false);
        accnAddBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        accnAddBtn.setName("accnAddBtn"); // NOI18N
        accnAddBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        defaultToolBar.add(accnAddBtn);

        accnRemoveBtn.setIcon(resourceMap.getIcon("accnRemoveBtn.icon")); // NOI18N
        accnRemoveBtn.setText(resourceMap.getString("accnRemoveBtn.text")); // NOI18N
        accnRemoveBtn.setToolTipText(resourceMap.getString("accnRemoveBtn.toolTipText")); // NOI18N
        accnRemoveBtn.setFocusable(false);
        accnRemoveBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        accnRemoveBtn.setName("accnRemoveBtn"); // NOI18N
        accnRemoveBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        defaultToolBar.add(accnRemoveBtn);

        jSeparator2.setName("jSeparator2"); // NOI18N
        defaultToolBar.add(jSeparator2);

        printBtn.setIcon(resourceMap.getIcon("printBtn.icon")); // NOI18N
        printBtn.setText(resourceMap.getString("printBtn.text")); // NOI18N
        printBtn.setToolTipText(resourceMap.getString("printBtn.toolTipText")); // NOI18N
        printBtn.setFocusable(false);
        printBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        printBtn.setName("printBtn"); // NOI18N
        printBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        defaultToolBar.add(printBtn);

        eadBtn.setAction(actionMap.get("convert2EAD")); // NOI18N
        eadBtn.setText(resourceMap.getString("eadBtn.text")); // NOI18N
        eadBtn.setToolTipText(resourceMap.getString("eadBtn.toolTipText")); // NOI18N
        eadBtn.setFocusable(false);
        eadBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        eadBtn.setName("eadBtn"); // NOI18N
        eadBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        defaultToolBar.add(eadBtn);

        jSeparator3.setName("jSeparator3"); // NOI18N
        defaultToolBar.add(jSeparator3);

        viewFileBtn.setAction(actionMap.get("previewFile")); // NOI18N
        viewFileBtn.setFocusable(false);
        viewFileBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        viewFileBtn.setName("viewFileBtn"); // NOI18N
        viewFileBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        defaultToolBar.add(viewFileBtn);

        techMetadataPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("techMetadataPanel.border.title"))); // NOI18N
        techMetadataPanel.setName("techMetadataPanel"); // NOI18N

        tm_nameLbl.setText(resourceMap.getString("tm_nameLbl.text")); // NOI18N
        tm_nameLbl.setName("tm_nameLbl"); // NOI18N

        tm_typLbl.setText(resourceMap.getString("tm_typLbl.text")); // NOI18N
        tm_typLbl.setName("tm_typLbl"); // NOI18N

        tm_lstModLbl.setText(resourceMap.getString("tm_lstModLbl.text")); // NOI18N
        tm_lstModLbl.setName("tm_lstModLbl"); // NOI18N

        tm_hiddenLbl.setText(resourceMap.getString("tm_hiddenLbl.text")); // NOI18N
        tm_hiddenLbl.setName("tm_hiddenLbl"); // NOI18N

        tm_sizeLbl.setText(resourceMap.getString("tm_sizeLbl.text")); // NOI18N
        tm_sizeLbl.setName("tm_sizeLbl"); // NOI18N

        tm_md5Lbl.setText(resourceMap.getString("tm_md5Lbl.text")); // NOI18N
        tm_md5Lbl.setName("tm_md5Lbl"); // NOI18N

        tm_md5.setText("");
        tm_md5.setName("tm_md5"); // NOI18N

        tm_lstMod.setText("");
        tm_lstMod.setName("tm_lstMod"); // NOI18N

        tm_size.setText("");
        tm_size.setName("tm_size"); // NOI18N

        tm_hidden.setText("");
        tm_hidden.setName("tm_hidden"); // NOI18N

        tm_name.setText("");
        tm_name.setName("tm_name"); // NOI18N

        tm_typ.setText("");
        tm_typ.setName("tm_typ"); // NOI18N

        org.jdesktop.layout.GroupLayout techMetadataPanelLayout = new org.jdesktop.layout.GroupLayout(techMetadataPanel);
        techMetadataPanel.setLayout(techMetadataPanelLayout);
        techMetadataPanelLayout.setHorizontalGroup(
            techMetadataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(techMetadataPanelLayout.createSequentialGroup()
                .add(techMetadataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(tm_md5Lbl)
                    .add(tm_nameLbl)
                    .add(tm_typLbl))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(techMetadataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(tm_typ, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(tm_name, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(tm_md5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(techMetadataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, tm_lstModLbl)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, tm_sizeLbl)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, tm_hiddenLbl))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(techMetadataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tm_lstMod)
                    .add(tm_size)
                    .add(tm_hidden))
                .addContainerGap(207, Short.MAX_VALUE))
        );
        techMetadataPanelLayout.setVerticalGroup(
            techMetadataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(techMetadataPanelLayout.createSequentialGroup()
                .add(techMetadataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(tm_nameLbl)
                    .add(tm_name))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(techMetadataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(tm_typLbl)
                    .add(tm_typ))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(techMetadataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(tm_md5Lbl)
                    .add(tm_md5)))
            .add(tm_lstMod)
            .add(techMetadataPanelLayout.createSequentialGroup()
                .add(tm_lstModLbl)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(techMetadataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(tm_sizeLbl)
                    .add(tm_size))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(techMetadataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(tm_hiddenLbl)
                    .add(tm_hidden)))
        );

        verticalScrollPane.setDividerLocation(200);
        verticalScrollPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        verticalScrollPane.setName("verticalScrollPane"); // NOI18N

        viewerPanel.setName("viewerPanel"); // NOI18N

        viewerSplitPane.setDividerLocation(250);
        viewerSplitPane.setName("viewerSplitPane"); // NOI18N

        filePanel.setName("filePanel"); // NOI18N
        filePanel.setLayout(new javax.swing.BoxLayout(filePanel, javax.swing.BoxLayout.PAGE_AXIS));

        fileLbl.setText(resourceMap.getString("fileLbl.text")); // NOI18N
        fileLbl.setName("fileLbl"); // NOI18N
        filePanel.add(fileLbl);

        fileSP.setName("fileSP"); // NOI18N

        fileTree.setModel(null);
        fileTree.setName("fileTree"); // NOI18N
        fileTree.setRootVisible(false);
        fileTree.setShowsRootHandles(true);
        fileSP.setViewportView(fileTree);

        filePanel.add(fileSP);

        viewerSplitPane.setLeftComponent(filePanel);

        seriesPanel.setName("seriesPanel"); // NOI18N
        seriesPanel.setLayout(new javax.swing.BoxLayout(seriesPanel, javax.swing.BoxLayout.PAGE_AXIS));

        seriesLbl.setText(resourceMap.getString("seriesLbl.text")); // NOI18N
        seriesLbl.setName("seriesLbl"); // NOI18N
        seriesPanel.add(seriesLbl);

        seriesSP.setName("seriesSP"); // NOI18N

        seriesTree.setModel(null);
        seriesTree.setName("seriesTree"); // NOI18N
        seriesTree.setRootVisible(false);
        seriesTree.setShowsRootHandles(true);
        seriesSP.setViewportView(seriesTree);

        seriesPanel.add(seriesSP);

        viewerSplitPane.setRightComponent(seriesPanel);

        org.jdesktop.layout.GroupLayout viewerPanelLayout = new org.jdesktop.layout.GroupLayout(viewerPanel);
        viewerPanel.setLayout(viewerPanelLayout);
        viewerPanelLayout.setHorizontalGroup(
            viewerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, viewerSplitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
        );
        viewerPanelLayout.setVerticalGroup(
            viewerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(viewerSplitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
        );

        verticalScrollPane.setLeftComponent(viewerPanel);

        metadataTabs.setName("metadataTabs"); // NOI18N

        defaultMetadataTab.setName("defaultMetadataTab"); // NOI18N

        defaultMetadata.setName("defaultMetadata"); // NOI18N

        altTitleLbl.setText(resourceMap.getString("altTitleLbl.text")); // NOI18N
        altTitleLbl.setName("altTitleLbl"); // NOI18N

        altTitleEdit.setName("altTitleEdit"); // NOI18N

        creatorEntityLbl.setText(resourceMap.getString("creatorEntityLbl.text")); // NOI18N
        creatorEntityLbl.setName("creatorEntityLbl"); // NOI18N

        creatorEntityEdit.setName("creatorEntityEdit"); // NOI18N

        creatorUnitLbl.setText(resourceMap.getString("creatorUnitLbl.text")); // NOI18N
        creatorUnitLbl.setName("creatorUnitLbl"); // NOI18N

        creatorUnitEdit.setName("creatorUnitEdit"); // NOI18N

        seriesLbl1.setText(resourceMap.getString("seriesLbl1.text")); // NOI18N
        seriesLbl1.setName("seriesLbl1"); // NOI18N

        seriesEdit.setEditable(true);
        seriesEdit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Series", "Sub Series 1", "Sub Series 2", "..." }));
        seriesEdit.setName("seriesEdit"); // NOI18N

        docType.setText(resourceMap.getString("docType.text")); // NOI18N
        docType.setName("docType"); // NOI18N

        docTypeEdit.setEditable(true);
        docTypeEdit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Letters (correspondence)", "Minutes", "Presentations", "..." }));
        docTypeEdit.setName("docTypeEdit"); // NOI18N

        restrictCkBx.setText(resourceMap.getString("restrictCkBx.text")); // NOI18N
        restrictCkBx.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        restrictCkBx.setName("restrictCkBx"); // NOI18N

        restrictYrsLbl.setText(resourceMap.getString("restrictYrsLbl.text")); // NOI18N
        restrictYrsLbl.setName("restrictYrsLbl"); // NOI18N

        restrictYrsEdit.setName("restrictYrsEdit"); // NOI18N

        restrictEdit.setEditable(true);
        restrictEdit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Item 1", "Item 2", "Item 3", "Item 4" }));
        restrictEdit.setName("restrictEdit"); // NOI18N

        excludeCkBx.setText(resourceMap.getString("excludeCkBx.text")); // NOI18N
        excludeCkBx.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        excludeCkBx.setName("excludeCkBx"); // NOI18N

        excludeEdit.setEditable(true);
        excludeEdit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Item 1", "Item 2", "Item 3", "Item 4" }));
        excludeEdit.setName("excludeEdit"); // NOI18N

        descLbl.setText(resourceMap.getString("descLbl.text")); // NOI18N
        descLbl.setName("descLbl"); // NOI18N

        descSP.setName("descSP"); // NOI18N

        descEdit.setMinimumSize(new java.awt.Dimension(75, 20));
        descEdit.setName("descEdit"); // NOI18N
        descSP.setViewportView(descEdit);

        org.jdesktop.layout.GroupLayout defaultMetadataLayout = new org.jdesktop.layout.GroupLayout(defaultMetadata);
        defaultMetadata.setLayout(defaultMetadataLayout);
        defaultMetadataLayout.setHorizontalGroup(
            defaultMetadataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(defaultMetadataLayout.createSequentialGroup()
                .add(defaultMetadataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(defaultMetadataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, altTitleLbl)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, creatorEntityLbl)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, creatorUnitLbl)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, seriesLbl1)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, docType))
                    .add(restrictCkBx, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(defaultMetadataLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(defaultMetadataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(descLbl)
                            .add(excludeCkBx))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(defaultMetadataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(defaultMetadataLayout.createSequentialGroup()
                        .add(restrictYrsLbl)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(restrictYrsEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(restrictEdit, 0, 299, Short.MAX_VALUE))
                    .add(descSP)
                    .add(excludeEdit, 0, 356, Short.MAX_VALUE)
                    .add(docTypeEdit, 0, 356, Short.MAX_VALUE)
                    .add(seriesEdit, 0, 356, Short.MAX_VALUE)
                    .add(creatorUnitEdit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                    .add(creatorEntityEdit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                    .add(altTitleEdit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE))
                .addContainerGap())
        );
        defaultMetadataLayout.setVerticalGroup(
            defaultMetadataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(defaultMetadataLayout.createSequentialGroup()
                .add(defaultMetadataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(altTitleLbl)
                    .add(altTitleEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(defaultMetadataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(creatorEntityLbl)
                    .add(creatorEntityEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(defaultMetadataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(creatorUnitLbl)
                    .add(creatorUnitEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(defaultMetadataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(seriesLbl1)
                    .add(seriesEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(defaultMetadataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(docType)
                    .add(docTypeEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(defaultMetadataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(restrictYrsLbl)
                    .add(restrictYrsEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(restrictEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(restrictCkBx))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(defaultMetadataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(excludeEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(excludeCkBx))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(defaultMetadataLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(defaultMetadataLayout.createSequentialGroup()
                        .add(descLbl)
                        .addContainerGap())
                    .add(descSP, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)))
        );

        defaultMetadataTab.setViewportView(defaultMetadata);

        metadataTabs.addTab(resourceMap.getString("defaultMetadataTab.TabConstraints.tabTitle"), defaultMetadataTab); // NOI18N

        rbmsclTab.setName("rbmsclTab"); // NOI18N
        metadataTabs.addTab(resourceMap.getString("rbmsclTab.TabConstraints.tabTitle"), rbmsclTab); // NOI18N

        uaMetadataTab.setName("uaMetadataTab"); // NOI18N
        metadataTabs.addTab(resourceMap.getString("uaMetadataTab.TabConstraints.tabTitle"), uaMetadataTab); // NOI18N

        verticalScrollPane.setRightComponent(metadataTabs);

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(defaultToolBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
            .add(techMetadataPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(verticalScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(defaultToolBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(verticalScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(techMetadataPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        projOpenM.setAction(actionMap.get("openProject")); // NOI18N
        projOpenM.setText(resourceMap.getString("projOpenM.text")); // NOI18N
        projOpenM.setName("projOpenM"); // NOI18N
        fileMenu.add(projOpenM);

        projNewM.setIcon(resourceMap.getIcon("project_new.icon")); // NOI18N
        projNewM.setText(resourceMap.getString("projNewM.text")); // NOI18N
        projNewM.setName("projNewM"); // NOI18N
        fileMenu.add(projNewM);

        jSeparator9.setName("jSeparator9"); // NOI18N
        fileMenu.add(jSeparator9);

        projSaveM.setAction(actionMap.get("saveProject")); // NOI18N
        projSaveM.setText(resourceMap.getString("projSaveM.text")); // NOI18N
        projSaveM.setName("projSaveM"); // NOI18N
        fileMenu.add(projSaveM);

        projCloseM.setAction(actionMap.get("closeProject")); // NOI18N
        projCloseM.setName("projCloseM"); // NOI18N
        fileMenu.add(projCloseM);

        projPrintM.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        projPrintM.setIcon(resourceMap.getIcon("print.icon")); // NOI18N
        projPrintM.setText(resourceMap.getString("projPrintM.text")); // NOI18N
        projPrintM.setName("projPrintM"); // NOI18N
        fileMenu.add(projPrintM);

        jSeparator10.setName("jSeparator10"); // NOI18N
        fileMenu.add(jSeparator10);

        exitM.setAction(actionMap.get("quit")); // NOI18N
        exitM.setName("exitM"); // NOI18N
        fileMenu.add(exitM);

        menuBar.add(fileMenu);

        showMenu.setText(resourceMap.getString("showMenu.text")); // NOI18N
        showMenu.setName("showMenu"); // NOI18N

        showSizeM.setAction(actionMap.get("displaySize")); // NOI18N
        showSizeM.setText(resourceMap.getString("showSizeM.text")); // NOI18N
        showSizeM.setName("showSizeM"); // NOI18N
        showMenu.add(showSizeM);

        showLastModM.setAction(actionMap.get("displayLastMod")); // NOI18N
        showLastModM.setText(resourceMap.getString("showLastModM.text")); // NOI18N
        showLastModM.setName("showLastModM"); // NOI18N
        showMenu.add(showLastModM);

        menuBar.add(showMenu);

        toolsMenu.setText(resourceMap.getString("toolsMenu.text")); // NOI18N
        toolsMenu.setName("toolsMenu"); // NOI18N

        accnAddM.setAction(actionMap.get("addAccession")); // NOI18N
        accnAddM.setText(resourceMap.getString("accnAddM.text")); // NOI18N
        accnAddM.setName("accnAddM"); // NOI18N
        toolsMenu.add(accnAddM);

        accnRmM.setIcon(resourceMap.getIcon("accession_remove.icon")); // NOI18N
        accnRmM.setText(resourceMap.getString("accnRmM.text")); // NOI18N
        accnRmM.setName("accnRmM"); // NOI18N
        toolsMenu.add(accnRmM);

        jSeparator11.setName("jSeparator11"); // NOI18N
        toolsMenu.add(jSeparator11);

        convertEadM.setAction(actionMap.get("convert2EAD")); // NOI18N
        convertEadM.setText(resourceMap.getString("convertEadM.text")); // NOI18N
        convertEadM.setName("convertEadM"); // NOI18N
        toolsMenu.add(convertEadM);

        previewFileM.setAction(actionMap.get("previewFile")); // NOI18N
        previewFileM.setIcon(resourceMap.getIcon("previewFileM.icon")); // NOI18N
        previewFileM.setText(resourceMap.getString("previewFileM.text")); // NOI18N
        previewFileM.setName("previewFileM"); // NOI18N
        toolsMenu.add(previewFileM);

        jSeparator12.setName("jSeparator12"); // NOI18N
        toolsMenu.add(jSeparator12);

        settingsM.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        settingsM.setIcon(resourceMap.getIcon("settings.icon")); // NOI18N
        settingsM.setText(resourceMap.getString("settingsM.text")); // NOI18N
        settingsM.setName("settingsM"); // NOI18N
        toolsMenu.add(settingsM);

        menuBar.add(toolsMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
            .add(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statusMessageLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 307, Short.MAX_VALUE)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(statusMessageLabel)
                    .add(statusAnimationLabel)
                    .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton accnAddBtn;
    private javax.swing.JMenuItem accnAddM;
    private javax.swing.JButton accnRemoveBtn;
    private javax.swing.JMenuItem accnRmM;
    private javax.swing.JTextField altTitleEdit;
    private javax.swing.JLabel altTitleLbl;
    private javax.swing.JMenuItem convertEadM;
    private javax.swing.JTextField creatorEntityEdit;
    private javax.swing.JLabel creatorEntityLbl;
    private javax.swing.JTextField creatorUnitEdit;
    private javax.swing.JLabel creatorUnitLbl;
    private javax.swing.JPanel defaultMetadata;
    private javax.swing.JScrollPane defaultMetadataTab;
    private javax.swing.JToolBar defaultToolBar;
    private javax.swing.JEditorPane descEdit;
    private javax.swing.JLabel descLbl;
    private javax.swing.JScrollPane descSP;
    private javax.swing.JLabel docType;
    private javax.swing.JComboBox docTypeEdit;
    private javax.swing.JButton eadBtn;
    private javax.swing.JCheckBox excludeCkBx;
    private javax.swing.JComboBox excludeEdit;
    private javax.swing.JLabel fileLbl;
    private javax.swing.JPanel filePanel;
    protected javax.swing.JScrollPane fileSP;
    protected javax.swing.JTree fileTree;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTabbedPane metadataTabs;
    private javax.swing.JMenuItem previewFileM;
    private javax.swing.JButton printBtn;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton projCloseBtn;
    private javax.swing.JMenuItem projCloseM;
    private javax.swing.JMenuItem projNewM;
    private javax.swing.JButton projOpenBtn;
    private javax.swing.JMenuItem projOpenM;
    private javax.swing.JMenuItem projPrintM;
    private javax.swing.JButton projSaveBtn;
    private javax.swing.JMenuItem projSaveM;
    private javax.swing.JScrollPane rbmsclTab;
    private javax.swing.JCheckBox restrictCkBx;
    private javax.swing.JComboBox restrictEdit;
    private javax.swing.JTextField restrictYrsEdit;
    private javax.swing.JLabel restrictYrsLbl;
    private javax.swing.JComboBox seriesEdit;
    private javax.swing.JLabel seriesLbl;
    private javax.swing.JLabel seriesLbl1;
    private javax.swing.JPanel seriesPanel;
    protected javax.swing.JScrollPane seriesSP;
    protected javax.swing.JTree seriesTree;
    private javax.swing.JMenuItem settingsM;
    private javax.swing.JCheckBoxMenuItem showLastModM;
    private javax.swing.JMenu showMenu;
    private javax.swing.JCheckBoxMenuItem showSizeM;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JPanel techMetadataPanel;
    protected javax.swing.JLabel tm_hidden;
    private javax.swing.JLabel tm_hiddenLbl;
    protected javax.swing.JLabel tm_lstMod;
    private javax.swing.JLabel tm_lstModLbl;
    protected javax.swing.JLabel tm_md5;
    private javax.swing.JLabel tm_md5Lbl;
    protected javax.swing.JLabel tm_name;
    private javax.swing.JLabel tm_nameLbl;
    protected javax.swing.JLabel tm_size;
    private javax.swing.JLabel tm_sizeLbl;
    protected javax.swing.JLabel tm_typ;
    private javax.swing.JLabel tm_typLbl;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JScrollPane uaMetadataTab;
    private javax.swing.JSplitPane verticalScrollPane;
    private javax.swing.JButton viewFileBtn;
    private javax.swing.JPanel viewerPanel;
    private javax.swing.JSplitPane viewerSplitPane;
    // End of variables declaration//GEN-END:variables
    /** DISPLAY **/
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
    protected JPopupMenu filePopup;
    protected JPopupMenu collectionPopup;
    /** METADATA DISPLAY **/
    List<ComponentQualifiedMetadata> metadataComponents =
            new ArrayList<ComponentQualifiedMetadata>(); //List of metadata data-entry fields.
    Document doc = null; //Used to temporarilly hold parsed XML documents.

    /**
     * Attempts to construct and display the metadata entry tabs and fields
     * indicated by the configuration file: conf/metadataTabs.conf. It resets
     * the existing tabs and their fields (stored in the metadataTabs ArrayList)
     * and for each tab calls createMetadataGroups. Each tab is placed within a 
     * scroll pane.
     * 
     * @throws java.lang.Exception
     */
    protected void loadMetadataTabs() throws Exception {
        metadataComponents.clear();
        metadataTabs.removeAll();
//         * Create a hash that associates a given input (checkbox, textfield, 
//         * etc) with an indication of the element (xPath notation? That would 
//         * include attributes...).
        doc = new SAXBuilder().build(new File("conf/metadataTabs.conf"));
        Object[] tabs = XPath.selectNodes(doc, "//metadataTabs/tab").toArray();
        for (Object tabObj : tabs) {
            Element tabElement = (Element) tabObj;
            //JScrollPane/JPanel combo
            JPanel tabPanel = createMetadataGroups(tabElement);
            JScrollPane tabSP = new JScrollPane(tabPanel);
            metadataTabs.add(tabElement.getAttributeValue("name"), tabSP);
        }
        doc = null; //Release resources
    // Future enhacement: Multi-select trees should disable 
    //                    non-multi edit fields.

    }

    /**
     * Performs an XPath query to return the Element containing the authority
     * list. Called by createMetadataGroup.
     * @param listId    is the name of the list (held by the field's "authority_list" attribute)
     * @return          the authority Element containing the authority list
     * @throws org.jdom.JDOMException
     */
    private Element requestMetadataAuthority(String listId) throws JDOMException {
        return (Element) (XPath.selectSingleNode(doc,
                "//authorities/authority[@name='" + listId + "']"));
    }

    /**
     * Sets the folder/item metadata displays to nothing.
     */
    protected void clearMetadata() {
        for (ComponentQualifiedMetadata metadataComponent : metadataComponents) {
            metadataComponent.clearComponentValue();
        }
        //Clear Metadata Tabs
        for (ComponentQualifiedMetadata qm : metadataComponents) {
            qm.clearComponentValue();
        }
        //Clear Technical Metadata
        tm_name.setText("");
        tm_lstMod.setText("");
        tm_md5.setText("");
        tm_size.setText("");
        tm_typ.setText("");
        tm_hidden.setText("");
    }

    /**
     * Generates the metadata data-entry tab's JPanels. Given the Element 
     * containing the metadata fields it identifies the appropriate JComponent
     * for entry, text area, text box, combo box, etc, arranges them in the
     * tab's JPanel using GridBag, and adds them to the metadataCompenents 
     * ArrayList.
     *  
     * @param tabElement    the Element containing the list of metadata elements
     * @return              JPanel tab
     */
    private JPanel createMetadataGroups(Element tabElement) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        try {
            List inputs = tabElement.getChildren();
            int row = 0;
            for (Object input : inputs) { //Row
                if (input instanceof Element) {
                    // Process input type (key/value, combo, boolean, group(recursive))

                    //Retrieve Element attributes
                    ComponentQualifiedMetadata qm =
                            new ComponentQualifiedMetadata(new JPanel());
                    qm.setElement(((Element) input).getAttributeValue("element"));
                    qm.setQualifier(((Element) input).getAttributeValue("qualifier"));
                    String type = ((Element) input).getAttributeValue("type");
                    String key = ((Element) input).getAttributeValue("id");
                    String label = ((Element) input).getAttributeValue("label");

                    //A label has been provided
                    if (label != null && !label.equalsIgnoreCase("")) {
                        GridBagConstraints displayConstraint =
                                new GridBagConstraints();
                        displayConstraint.gridy = row;
                        displayConstraint.gridx = 0;
                        displayConstraint.anchor =
                                GridBagConstraints.FIRST_LINE_END;
                        displayConstraint.insets = new Insets(2, 2, 0, 0);
                        panel.add(new JLabel(label), displayConstraint);
                    }

                    //GridBag prep
                    GridBagConstraints componentConstraint =
                            new GridBagConstraints();
                    componentConstraint.gridy = row;
                    componentConstraint.gridx = GridBagConstraints.RELATIVE;
                    componentConstraint.gridwidth = GridBagConstraints.REMAINDER;
                    componentConstraint.fill = GridBagConstraints.HORIZONTAL;
                    componentConstraint.insets = new Insets(2, 2, 0, 2);
                    componentConstraint.weightx = 1.0;

                    //TEXTFIELD
                    if (type.equalsIgnoreCase("text_field")) {
                        qm.component = new JTextField(30);
                        String width =
                                ((Element) input).getAttributeValue("width");
                        if (width != null) {
                            try {
                                ((JTextField) qm.component).setColumns(Integer.parseInt(width));
                            } catch (NumberFormatException nfe) {
                                System.err.println("Can't set " + key + " " +
                                        type + " width as non-integer " + width);
                            }

                        }
                        String defaultText =
                                ((Element) input).getAttributeValue("default");
                        if (defaultText != null &&
                                !defaultText.equalsIgnoreCase("")) {
                            ((JTextArea) qm.component).setText(defaultText);
                        }
                        panel.add(qm.component, componentConstraint);
                    } //TEXTAREA
                    else if (type.equalsIgnoreCase("text_area")) {
                        qm.component = new JTextArea(4, 30);
                        componentConstraint.fill = GridBagConstraints.BOTH;
                        String width =
                                ((Element) input).getAttributeValue("width");
                        if (width != null) {
                            try {
                                ((JTextArea) qm.component).setColumns(Integer.parseInt(width));
                            } catch (NumberFormatException nfe) {
                                System.err.println("Can't set " + key + " " +
                                        type + "width as non-integer " + width);
                            }
                        }
                        String rows =
                                ((Element) input).getAttributeValue("rows");
                        if (rows != null) {
                            try {
                                ((JTextArea) qm.component).setRows(Integer.parseInt(rows));
                            } catch (NumberFormatException nfe) {
                                System.err.println("Can't set " + key + " " +
                                        type + "rows as non-integer " + rows);
                            }

                        }
                        String defaultText =
                                ((Element) input).getAttributeValue("default");
                        if (defaultText != null &&
                                !defaultText.equalsIgnoreCase("")) {
                            ((JTextArea) qm.component).setText(defaultText);
                        }
                        JScrollPane jsp = new JScrollPane(qm.component);
                        panel.add(jsp, componentConstraint);
                    } //DROPDOWN (ComboBox)
                    else if (type.equalsIgnoreCase("drop_down")) {
                        //Get list of Values
                        Element authorityElement =
                                requestMetadataAuthority(((Element) input).getAttributeValue("authority_list"));
                        List<String> options = new ArrayList<String>();
                        List optionElements =
                                authorityElement.getChildren("option");
                        for (Object optionElement : optionElements) {
                            options.add(((Element) optionElement).getTextNormalize());
                        }
                        qm.component = new JComboBox(options.toArray());
                        //Enable the user to add text
                        String editable =
                                ((Element) input).getAttributeValue("editable");
                        if (editable != null &&
                                editable.equalsIgnoreCase("true")) {
                            ((JComboBox) qm.component).setEditable(true);
                            ((JComboBox) qm.component).addActionListener(new ActionListener() {

                                public void actionPerformed(ActionEvent arg0) {
                                    JComboBox jcb = (JComboBox) arg0.getSource();
                                    for (int i = 0; i < jcb.getItemCount(); i++) {
                                        if (((String) jcb.getItemAt(i)).equalsIgnoreCase(((String) jcb.getSelectedItem()))) {
                                            return;
                                        }
                                    }
                                    if (jcb.getSelectedItem() != null) {
                                        jcb.addItem(jcb.getSelectedItem());
                                    }
                                }
                            });
                        }

                        panel.add(qm.component, componentConstraint);
                    } //CHECKBOX
                    //FIXME: Booleans do not update in the metadata correctly. The problem is not located here, but this probably should not be used until the bug is fixed.
                    else if (type.equalsIgnoreCase("boolean")) {
                        qm.component = new JCheckBox();
                        String defaultText =
                                ((Element) input).getAttributeValue("default");
                        if (defaultText != null &&
                                defaultText.equalsIgnoreCase("true")) {
                            ((JCheckBox) qm.component).setSelected(true);
                        }
                        panel.add(qm.component, componentConstraint);
                    }
                    metadataComponents.add(qm);
                //Cases that recurse (we don't want to map these(yet)).
                }
                row++;
            }
            return panel;
        } catch (Exception e) {
            //return blank space
            System.err.println("Couldn't create metadata tab panel: " +
                    e.getLocalizedMessage());
            e.printStackTrace();
            panel.removeAll();
            panel.setSize(0, 0);
        }
        return panel;
    }
    /** TREE DISPLAY/CHANGE **/
//    MetsTreePanel fileTreePanel = null; //Holds Accessions Tree
//    MetsTreePanel seriesTreePanel = null; //Holds Collection Series Tree.
    private JTree treeEventOriginator = null; //Simple pointer so we know who initiated the JTree valueChanged so we can clear the other.

    /**
     * Sets a boolean modifier telling the display to (or not to) display the
     * file's last modified date by its name.
     */
    @Action
    public void displayLastMod() {
//        boolean displayLastMod = !fileTreePanel.displayLastModified();
        showLastModM.setSelected(!showLastModM.isSelected());
//        fileTreePanel.displayLastModified(displayLastMod);
//        seriesTreePanel.displayLastModified(displayLastMod);
//        fileSP.repaint();
//        seriesSP.repaint();
        fileSP.updateUI();
        seriesSP.updateUI();
    }

    /**
     * Sets a boolean modifier telling the display to (or not to) display the
     * file's last modified date by its name.
     */
    @Action
    public void displaySize() {
//        boolean displaySize = !fileTreePanel.displaySize();
        showSizeM.setSelected(!showSizeM.isSelected());
//        fileTreePanel.displaySize(displaySize);
//        seriesTreePanel.displaySize(displaySize);
//        fileSP.repaint();
//        seriesSP.repaint();
        fileSP.updateUI();
        seriesSP.updateUI();
    }

    /**
     * Handles requested actions on the accession and file trees. Currently only 
     * "right-click" actions are implemented. Currently does not get called for
     * multiple elements selection although this code is designed to handle it.
     * 
     * @param e triggered ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
        //Right-Click actions
        System.out.println("Action :" + e.getActionCommand());
        //INSERT A COLLECTION FOLDER (DIV)
        if (e.getActionCommand().equalsIgnoreCase("insertCollDiv")) {
            String s = (String) JOptionPane.showInputDialog(
                    this.getFrame(),
                    "Please enter a name.",
                    "Enter Item Name",
                    JOptionPane.PLAIN_MESSAGE);
            if (s != null && !s.matches("^\\s$")) {
                if (seriesTree.getSelectionCount() > 0) {
                    if (!(((MetsNode) seriesTree.getLastSelectedPathComponent()).addDiv(s))) {
                        JOptionPane.showMessageDialog(this.getFrame(),
                                "Unable to create folder: " + s);
                    }
                } else { //Child of Collection structMap
                    if (!(((MetsNode) seriesTree.getModel().getRoot()).addDiv(s))) {
                        JOptionPane.showMessageDialog(this.getFrame(),
                                "Unable to create folder: " + s);
                    }
                }
                seriesTree.expandPath(seriesTree.getSelectionPath());
            }
        } //REMOVE AN ITEM/FOLDER
        else if (e.getActionCommand().equalsIgnoreCase("removeCollectionltem")) {
            String confirmMessage = "Are you sure you want to remove ";
            if (seriesTree.getSelectionCount() > 1) {
                confirmMessage += "all the selected items";
            } else if (seriesTree.getSelectionCount() == 1) {
                confirmMessage += seriesTree.getSelectionPath().
                        getLastPathComponent().toString();
            }
            confirmMessage += "?";
            int n = JOptionPane.showConfirmDialog(
                    this.getFrame(),
                    confirmMessage,
                    "Confirm Remove",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                for (TreePath path : seriesTree.getSelectionPaths()) {
                    ((MetsNode)path.getLastPathComponent()).removeFromParent();
//                    ((MetsNode) path.getParentPath().getLastPathComponent()).removeChild(path.getLastPathComponent());
                }
            }
        } else if(e.getActionCommand().startsWith("preview:")){
            //Split the string on : into "preview" and "[browser to use]"
            String browser = e.getActionCommand().substring(8);
            //Call the preview function passing the [browser to use]
            Task preview = Application.getInstance(edu.duke.archives.erprocessor.ERProcessor.class).previewFile(browser);
            preview.execute();
        }
        seriesTree.updateUI();
    }

    /**
     * Called when a new part of either tree is selected. Will only allow one 
     * tree to be selected at a time to prevent unintended metadata travel.
     * Clearing the other tree will cause an additional call to valueChanged so 
     * the treeEventOriginator pointer tells us who should now have focus.
     * Nodes which lose focus will have their metadata updated.
     * 
     * @param e TreeSelectionEvent
     */
    public void valueChanged(TreeSelectionEvent e) {
        this.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (e.getSource() instanceof JTree) {
            JTree source = (JTree) e.getSource();

            //Identify the originator of the change
            if (treeEventOriginator == null) {
                treeEventOriginator = source;
                //Take care of the other tree
                JTree otherTree = getOtherTree(source);
                if (otherTree.getSelectionCount() > 0) {
                    otherTree.clearSelection();
                }
            }

            //SELECTED TREE
            //Update nodes first
            for (TreePath path : e.getPaths()) {
                if (!e.isAddedPath(path)) {//Was removed
                    if (path.getLastPathComponent() instanceof MetsNode) {
                        updateNode((MetsNode) path.getLastPathComponent(),
                                source);
                    }
                }
            }
            clearMetadata();
            //Load new nodes second
            if (source.getSelectionCount() == 1) { //Only one selected
                if (source.getSelectionPath().getLastPathComponent() instanceof MetsNode) {
                    MetsNode node = (MetsNode) source.getSelectionPath().
                            getLastPathComponent();
                    if (node instanceof MetsFile) {
                        tm_name.setText(node.toString());
                        //Technical Metadata
                        tm_lstMod.setText(((MetsFile) node).getLastModified());
                        tm_size.setText(ERProcessor.prettySize(((MetsFile) node).getSize()));
                        tm_md5.setText(((MetsFile) node).getCHECKSUM());
                        //TODO: Check File Format
                        tm_typ.setText(((MetsFile) node).getFileType());
                    }

                    //Qualified/Tabbed Metadata
                    for (ComponentQualifiedMetadata qmComp : metadataComponents) {
                        qmComp.clearComponentValue(); //Executes component-sepcific clear

                        List<QualifiedMetadata> qmList =
                                node.getQualifiedMetadata();

                        for (QualifiedMetadata qm : qmList) {
//                            if (((qmComp.getQualifier() == null) || qmComp.getQualifier().
//                                    isEmpty()) && (qm.getQualifier() == null || qm.getQualifier().
//                                    isEmpty())) {
//                                qmComp.setComponentValue(new String[]{qm.getValue()});
//                                break;
//                            } else {
//                                if (qmComp.getQualifier().equalsIgnoreCase(qm.getQualifier())) {
//                                    qmComp.setComponentValue(new String[]{qm.getValue()});
//                                    break;
//                                }
//                            }
                            //Matching Element & Qualifier?
                            if (qm.getElement().equalsIgnoreCase(qmComp.getElement()) && qm.getQualifier().
                                    equalsIgnoreCase(qmComp.getQualifier())) {
                                //Matching Qualifier?
                                qmComp.setComponentValue(new String[]{qm.getValue()});
                                break;
                            }
                        }
                    }
                }
            }
            // TODO: Multiple items selected
            if (treeEventOriginator.equals(source)) {
                treeEventOriginator = null; //Relinquish if it is ours to do so
            }
        }
        this.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Updates the metadata for passed mets node. Iterates through the 
     * metadataComponents ArrayList looking for value changes before update and 
     * adding values if they did not already exisit.
     * 
     * @param node   MetsNode to be updated
     * @param source    JTree holding the node
     */
    private void updateNode(MetsNode node, JTree source) {

        List<QualifiedMetadata> qmList = node.getQualifiedMetadata();
        for (ComponentQualifiedMetadata qmComp : metadataComponents) {
            for (String value : qmComp.getValues()) {
                boolean noMatch = true;
                //Compare to existing children
                for (QualifiedMetadata qm : qmList) {
                    //Element match?
                    String name = qm.getElement();
                    String qualifier = qm.getQualifier();
                    qualifier = (qualifier == null) ? "" : qualifier;
                    String compElement = qmComp.getElement();
                    String compQualifier = qmComp.getQualifier();
                    if (name != null &&
                            (compElement.equalsIgnoreCase(name)) &&
                            (compQualifier.equalsIgnoreCase(qualifier))) {
                        noMatch = false;
                        //Should we update the match?
                        if (!value.equals(qm.getValue())) {
                            qm.setValue(value);
                        }
                    }
                }

                if (noMatch && ! qmComp.getValue().equalsIgnoreCase("")) {
                    //Ensure container to store update
                    QualifiedMetadata newMetadata =
                            new QualifiedMetadata();
                    newMetadata.setElement(qmComp.getElement());
                    if (!qmComp.getQualifier().equalsIgnoreCase("")) {
                        newMetadata.setQualifier(qmComp.getQualifier());
                    }
                    newMetadata.setValue(value);
                    qmList.add(newMetadata);
                }
            }
        }
        node.setQualifiedMetadata(qmList);
    }

    /**
     * Evaluates which tree (seriesTree or fileTree) the passed reference 
     * refers to and returns the other.
     * 
     * @param tree  the JTree reference to check
     * @return  the other JTree
     */
    private JTree getOtherTree(JTree tree) {
        if (tree.equals(fileTree)) {
            return seriesTree;
        } else {
            return fileTree;
        }
    }

    /** UTILITY ACTIONS **/
    
    public void setMessage(String message){
        statusMessageLabel.setText(message);
    }
    
    public void setSaveEnabled(boolean saveEnabled) {
        boolean oldValue = this.saveEnabled;
        this.saveEnabled = saveEnabled;
        firePropertyChange("saveEnabled", oldValue, this.saveEnabled);
    }
    
    public boolean isSaveEnabled(){
        return saveEnabled;
    }
    
    @Action
    public Task addAccession() {
        JFileChooser jfc = new JFileChooser();
        int returnVal = jfc.showOpenDialog(this.getFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File accessionFile = jfc.getSelectedFile();
            return new AddAccessionTask(getApplication(), accessionFile,
                    this.getFrame());
        }
        return null;
    }

    private class AddAccessionTask extends org.jdesktop.application.Task<Object, Void> {

        File accessionFile;
        JFrame frame;

        AddAccessionTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to AddAccessionTask fields, here.
            super(app);
            
        }

        private AddAccessionTask(org.jdesktop.application.Application application,
                File accessionFile, JFrame frame) {
            super(application);
            this.accessionFile = accessionFile;
            this.frame = frame;
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            statusMessageLabel.setText("Loading accession file: "+accessionFile.getName()+"... Please be patient.");
            
        }

        @Override
        protected Object doInBackground() {
            System.out.println("Loading accession into project with file: " +
                    accessionFile.getPath());
            ((ERProcessor) this.getApplication()).loadAccession(accessionFile);
            return null;
        }

        @Override
        protected void succeeded(Object result) {
            fileTree.updateUI();
            if(! isSaveEnabled()){
                setSaveEnabled(true);
            }
        }

        @Override
        protected void finished() {
            frame.setCursor(Cursor.getDefaultCursor());
//            System.out.println("Done loading accession file.");
            statusMessageLabel.setText("Done loading accession file.");
            super.finished();
        }
    }

    @Action
    public void convert2EAD() {
        new Convert2EADTask(getApplication(), this.getFrame()).run();
    }

    private class Convert2EADTask extends org.jdesktop.application.Task<Object, Void> {

        JFrame frame;

        private Convert2EADTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to Convert2EADTask fields, here.
            super(app);
        }

        public Convert2EADTask(org.jdesktop.application.Application app,
                JFrame frame) {
            super(app);
            this.frame = frame;
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }

        @Override
        protected Object doInBackground() {
            // Your Task's code here.  This method runs
            // on a background thread, so don't reference
            // the Swing GUI from here.
            ((ERProcessor) this.getApplication()).createEAD();
            return null;
        }

        @Override
        protected void succeeded(Object result) {

            frame.setCursor(Cursor.getDefaultCursor());
            statusMessageLabel.setText("Done converting EAD.");
            super.finished();
        }
    }

    @Action
    public Task openProject() {
        JFileChooser jfc = new JFileChooser();
        int returnVal = jfc.showOpenDialog(this.getFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File projectFile = jfc.getSelectedFile();
            return new OpenProjectTask(this.getApplication(), projectFile);
        }
        return null;
    }

    private class OpenProjectTask extends org.jdesktop.application.Task<Object, Void> {

        File projectFile;

        OpenProjectTask(org.jdesktop.application.Application app) {
            super(app);
        }

        private OpenProjectTask(Application application, File projectFile) {
            this(application);
            this.projectFile = projectFile;
        }

        @Override
        protected Object doInBackground() {
            statusMessageLabel.setText("Loading Project with file: " + projectFile.getPath());
            ((ERProcessor) this.getApplication()).loadProject(projectFile);
            return null;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            statusMessageLabel.setText("Done loading project file.");
            setSaveEnabled(true);
        }
    }

    @Action(enabledProperty = "saveEnabled")
    public Task saveProject() {
        //This will fire valueChanged and update metadata before save.
        if (fileTree.getSelectionCount() > 0) {
            fileTree.clearSelection();
        }
        if (seriesTree.getSelectionCount() > 0) {
            seriesTree.clearSelection();
        }
        return new SaveProjectTask(getApplication(), this.getFrame());
    }

    private class SaveProjectTask extends org.jdesktop.application.Task<Object, Void> {

        JFrame frame;

        SaveProjectTask(org.jdesktop.application.Application app, JFrame jFrame) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to SaveProjectTask fields, here.
            super(app);
            frame = jFrame;
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }

        @Override
        protected Object doInBackground() {
            try {
                ((ERProcessor) this.getApplication()).saveProject();
                return null; // return your result
            } catch (Exception ex) {
                Logger.getLogger(ERProcessorView.class.getName()).
                        log(Level.SEVERE, null, ex);
                return ex.getLocalizedMessage();
            }
        }

        @Override
        protected void succeeded(Object result) {
            if (result == null) {
                JOptionPane.showMessageDialog(frame, "Project saved.");
                frame.setCursor(Cursor.getDefaultCursor());
                super.finished();
            } else {
                String message =
                        "An unknown error occured while trying to save the project.";
                if (result instanceof String) {
                    message = (String) result;
                }
                JOptionPane.showMessageDialog(frame, message,
                        "Error Saving Project", JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override
        protected void finished() {
            frame.setCursor(Cursor.getDefaultCursor());
            statusMessageLabel.setText("Done saving project file.");
            super.finished();
        }
    }

    @Action
    public void closeProject() {
        int n = JOptionPane.showConfirmDialog(
                this.getFrame(),
                "Do you want to save before closing?",
                "Save Before Close?",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            try {
                ((ERProcessor) this.getApplication()).saveProject();
            } catch (Exception ex) {
                Logger.getLogger(ERProcessorView.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
        if (n == JOptionPane.YES_OPTION || n == JOptionPane.NO_OPTION) {
            setSaveEnabled(false);
            doc = null;
            clearMetadata();
            fileTree = null;
            seriesTree = null;
            seriesSP.setViewport(null);
            fileSP.setViewport(null);
            ((ERProcessor) this.getApplication()).closeProject();
        }
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = ERProcessor.getApplication().getMainFrame();
            aboutBox = new ERProcessorAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        ERProcessor.getApplication().show(aboutBox);
    }
}
