package org.eclipse.cdt.internal.ui.editor;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ISourceRange;
import org.eclipse.cdt.core.model.ISourceReference;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.core.model.IWorkingCopy;
import org.eclipse.cdt.internal.ui.IContextMenuConstants;
import org.eclipse.cdt.internal.ui.editor.asm.AsmTextTools;
import org.eclipse.cdt.internal.ui.text.CPairMatcher;
import org.eclipse.cdt.internal.ui.text.CSourceViewerConfiguration;
import org.eclipse.cdt.internal.ui.text.CTextTools;
import org.eclipse.cdt.internal.ui.text.ContentAssistPreference;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.IWorkingCopyManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.OverviewRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.editors.text.TextEditorPreferenceConstants;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.tasklist.TaskList;


/**
 * C specific text editor.
 */
public class CEditor extends TextEditor implements ISelectionChangedListener {

	/** The outline page */
	protected CContentOutlinePage fOutlinePage;

	private FileSearchAction fFileSearchAction;

	private FileSearchActionInWorkingSet fFileSearchActionInWorkingSet;
	
	private SearchDialogAction fSearchDialogAction;
	
	protected ISelectionChangedListener fStatusLineClearer;
    
    /** The property change listener */
    private PropertyChangeListener fPropertyChangeListener = new PropertyChangeListener();

	protected final static char[] BRACKETS = { '{', '}', '(', ')', '[', ']' };

	protected CPairMatcher fBracketMatcher = new CPairMatcher(BRACKETS);

	/** The editor's tab converter */
	private TabConverter fTabConverter;

	private MarkerAnnotationPreferences fAnnotationPreferences;

	/** Listener to annotation model changes that updates the error tick in the tab image */
	private CEditorErrorTickUpdater fCEditorErrorTickUpdater;

	/* Preference key for matching brackets */
	public final static String MATCHING_BRACKETS = "matchingBrackets";
	/* Preference key for matching brackets color */
	public final static String MATCHING_BRACKETS_COLOR = "matchingBracketsColor";
	/** Preference key for inserting spaces rather than tabs */
	public final static String SPACES_FOR_TABS = "spacesForTabs";
	/** Preference key for linked position color */
	public final static String LINKED_POSITION_COLOR = "linkedPositionColor"; //$NON-NLS-1$

    /** Preference key for compiler task tags */
    private final static String TRANSLATION_TASK_TAGS= CCorePlugin.TRANSLATION_TASK_TAGS;

    private class PropertyChangeListener implements org.eclipse.core.runtime.Preferences.IPropertyChangeListener, org.eclipse.jface.util.IPropertyChangeListener {      
        /*
         * @see IPropertyChangeListener#propertyChange(PropertyChangeEvent)
         */
        public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
            handlePreferencePropertyChanged(event);
        }
        public void propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent event) {
            handlePreferencePropertyChanged(new org.eclipse.jface.util.PropertyChangeEvent(event.getSource(), event.getProperty(), event.getOldValue(), event.getNewValue()));
        }
    };        

	/**
	 * Default constructor.
	 */
	public CEditor() {
		super();
		fAnnotationPreferences = new MarkerAnnotationPreferences();
		CTextTools textTools = CUIPlugin.getDefault().getTextTools();
		setSourceViewerConfiguration(new CSourceViewerConfiguration(textTools, this));
		setDocumentProvider(CUIPlugin.getDefault().getDocumentProvider());
		setRangeIndicator(new DefaultRangeIndicator());
		setPreferenceStore(CUIPlugin.getDefault().getPreferenceStore());

		setEditorContextMenuId("#CEditorContext"); //$NON-NLS-1$
		setRulerContextMenuId("#CEditorRulerContext"); //$NON-NLS-1$
		setOutlinerContextMenuId("#CEditorOutlinerContext"); //$NON-NLS-1$

		fCEditorErrorTickUpdater = new CEditorErrorTickUpdater(this);          
	}

	/**
	 * @see AbstractTextEditor#doSetInput(IEditorInput)
	 */
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		fCEditorErrorTickUpdater.setAnnotationModel(getDocumentProvider().getAnnotationModel(input));
		setOutlinePageInput(fOutlinePage, input);
	}

	/**
	 * Update the title image
	 */
	public void updatedTitleImage(Image image) {
		setTitleImage(image);
	}

	/**
	 * Gets the current input
	 */
	public IFile getInputFile() {
		//IFileEditorInput editorInput = (IFileEditorInput)getEditorInput();
		IEditorInput editorInput = (IEditorInput) getEditorInput();
		if (editorInput != null) {
			if ((editorInput instanceof IFileEditorInput)) {
				return ((IFileEditorInput) editorInput).getFile();
			}
		}
		return null;
	}

	public boolean isSaveAsAllowed() {
		return true;
	}
	/**
	 * Gets the outline page of the c-editor
	 */
	public CContentOutlinePage getOutlinePage() {
		if (fOutlinePage == null) {
			fOutlinePage = new CContentOutlinePage(this);
			fOutlinePage.addSelectionChangedListener(this);
		}
		return fOutlinePage;
	}

	/**
	 * @see AbstractTextEditor#getAdapter(Class)
	 */
	public Object getAdapter(Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			return getOutlinePage();
		}
		return super.getAdapter(required);
	}
	/**
	 * Handles a property change event describing a change
	 * of the editor's preference store and updates the preference
	 * related editor properties.
	 * 
	 * @param event the property change event
	 */
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
		AdaptedSourceViewer asv = (AdaptedSourceViewer) getSourceViewer();

		try {
			if (asv != null) {

				String property = event.getProperty();

				if (CSourceViewerConfiguration.PREFERENCE_TAB_WIDTH.equals(property)) {
					SourceViewerConfiguration configuration = getSourceViewerConfiguration();
					String[] types = configuration.getConfiguredContentTypes(asv);
					for (int i = 0; i < types.length; i++)
						asv.setIndentPrefixes(configuration.getIndentPrefixes(asv, types[i]), types[i]);

					if (fTabConverter != null)
						fTabConverter.setNumberOfSpacesPerTab(
							getPreferenceStore().getInt(CSourceViewerConfiguration.PREFERENCE_TAB_WIDTH));

					Object value = event.getNewValue();

					if (value instanceof Integer) {
						asv.getTextWidget().setTabs(((Integer) value).intValue());

					} else if (value instanceof String) {
						asv.getTextWidget().setTabs(Integer.parseInt((String) value));
					}
				}

				if (SPACES_FOR_TABS.equals(property)) {
					if (isTabConversionEnabled())
						startTabConversion();
					else
						stopTabConversion();
					return;
				}
				
				IContentAssistant c= asv.getContentAssistant();
				if (c instanceof ContentAssistant)
					ContentAssistPreference.changeConfiguration((ContentAssistant) c, getPreferenceStore(), event);
				
			}
		} finally {
			super.handlePreferenceStoreChanged(event);
		}
	}

	/**
	 * @see ISelectionChangedListener#selectionChanged
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection sel = event.getSelection();
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) sel;
			Object obj = selection.getFirstElement();
			if (obj instanceof ISourceReference) {
				try {
					ISourceRange range = ((ISourceReference) obj).getSourceRange();
					if (range != null) {
						setSelection(range, !isActivePart());
					}
				} catch (CModelException e) {
				}
			}
		}
	}

	public void setSelection(ICElement element) {

		if (element == null || element instanceof ITranslationUnit) {
			/*
			 * If the element is an ITranslationUnit this unit is either the input
			 * of this editor or not being displayed. In both cases, nothing should
			 * happened.
			 */
			return;
		}
		if (element instanceof ISourceReference) {
			ISourceReference reference = (ISourceReference) element;
			// set hightlight range
			setSelection(reference, true);
			// set outliner selection
			//if (fOutlinePage != null) {
			//	fOutlinePage.removeSelectionChangedListener(fSelectionChangedListener);
			//	fOutlinePage.select(reference);
			//	fOutlinePage.addSelectionChangedListener(fSelectionChangedListener);
			//}
		}
	}

	public void setSelection(ISourceReference element, boolean moveCursor) {
		if (element != null) {
			try {
				setSelection(element.getSourceRange(), moveCursor);
			} catch (CModelException e) {
			}
		}
	}

	/**
	 * Sets the current editor selection to the source range. Optionally
	 * sets the current editor position.
	 *
	 * @param element the source range to be shown in the editor, can be null.
	 * @param moveCursor if true the editor is scrolled to show the range.
	 */
	public void setSelection(ISourceRange element, boolean moveCursor) {

		if (element == null) {
			return;
		}

		try {
			IRegion alternateRegion = null;
			int start = element.getStartPos();
			int length = element.getLength();

			// Sanity check sometimes the parser may throw wrong numbers.
			if (start < 0 || length < 0) {
				start = 0;
				length = 0;
			}

			// 0 length and start and non-zero start line says we know
			// the line for some reason, but not the offset.
			if (length == 0 && start == 0 && element.getStartLine() > 0) {
				// We have the information in term of lines, we can work it out.
				// Binary elements return the first executable statement so we have to substract -1
				start = getDocumentProvider().getDocument(getEditorInput()).getLineOffset(element.getStartLine() - 1);
				if (element.getEndLine() > 0) {
					length = getDocumentProvider().getDocument(getEditorInput()).getLineOffset(element.getEndLine()) - start;
				} else {
					length = start;
				}
				// create an alternate region for the keyword highlight.
				alternateRegion = getDocumentProvider().getDocument(getEditorInput()).getLineInformation(element.getStartLine() - 1);
				if (start == length || length < 0) {
					if (alternateRegion != null) {
						start = alternateRegion.getOffset();
						length = alternateRegion.getLength();
					}
				}
			}
			setHighlightRange(start, length, moveCursor);

			if (moveCursor) {
				start = element.getIdStartPos();
				length = element.getIdLength();
				if (start == 0 && length == 0 && alternateRegion != null) {
					start = alternateRegion.getOffset();
					length = alternateRegion.getLength();
				}
				if (start > -1 && getSourceViewer() != null) {
					getSourceViewer().revealRange(start, length);
					getSourceViewer().setSelectedRange(start, length);
				}
				updateStatusField(CTextEditorActionConstants.STATUS_CURSOR_POS);
			}
			return;
		} catch (IllegalArgumentException x) {
		} catch (BadLocationException e) {
		}

		if (moveCursor)
			resetHighlightRange();
	}

	private boolean isActivePart() {
		IWorkbenchWindow window = getSite().getWorkbenchWindow();
		IPartService service = window.getPartService();
		return (this == service.getActivePart());
	}

	public void dispose() {

		if (fCEditorErrorTickUpdater != null) {
			fCEditorErrorTickUpdater.setAnnotationModel(null);
			fCEditorErrorTickUpdater = null;
		}
		if (fBracketMatcher != null) {
			fBracketMatcher.dispose();
			fBracketMatcher = null;
		}
        if (fPropertyChangeListener != null) {
			Preferences preferences = CCorePlugin.getDefault().getPluginPreferences();
			preferences.removePropertyChangeListener(fPropertyChangeListener);			
			IPreferenceStore preferenceStore = getPreferenceStore();
			preferenceStore.removePropertyChangeListener(fPropertyChangeListener);
        }
		super.dispose();
	}

	protected void createActions() {
		super.createActions();

		// Default text editing menu items

		IAction action = new TextOperationAction(CEditorMessages.getResourceBundle(), "Comment.", this, ITextOperationTarget.PREFIX); //$NON-NLS-1$
		action.setActionDefinitionId(ICEditorActionDefinitionIds.COMMENT);
		setAction("Comment", action); //$NON-NLS-1$
		markAsStateDependentAction("Comment", true); //$NON-NLS-1$

		action = new TextOperationAction(CEditorMessages.getResourceBundle(), "Uncomment.", this, ITextOperationTarget.STRIP_PREFIX); //$NON-NLS-1$
		action.setActionDefinitionId(ICEditorActionDefinitionIds.UNCOMMENT);
		setAction("Uncomment", action); //$NON-NLS-1$
		markAsStateDependentAction("Uncomment", true); //$NON-NLS-1$

		action = new TextOperationAction(CEditorMessages.getResourceBundle(), "Format.", this, ISourceViewer.FORMAT); //$NON-NLS-1$
		action.setActionDefinitionId(ICEditorActionDefinitionIds.FORMAT);
		setAction("Format", action); //$NON-NLS-1$
		markAsStateDependentAction("Format", true); //$NON-NLS-1$

		action = new ContentAssistAction(CEditorMessages.getResourceBundle(), "ContentAssistProposal.", this); //$NON-NLS-1$
		action.setActionDefinitionId(ICEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction("ContentAssistProposal", action);
		markAsStateDependentAction("ContentAssistProposal", true); //$NON-NLS-1$

		action = new TextOperationAction(CEditorMessages.getResourceBundle(), "ContentAssistTip.", this, ISourceViewer.CONTENTASSIST_CONTEXT_INFORMATION); //$NON-NLS-1$
		action.setActionDefinitionId(ICEditorActionDefinitionIds.CONTENT_ASSIST_CONTEXT_INFORMATION);
		setAction("ContentAssistTip", action);

		setAction("AddIncludeOnSelection", new AddIncludeOnSelectionAction(this)); //$NON-NLS-1$
	
		action = new OpenDeclarationsAction(this);
		action.setActionDefinitionId(ICEditorActionDefinitionIds.OPEN_DECL);
		setAction("OpenDeclarations", action);

		fFileSearchAction = new FileSearchAction(getSelectionProvider());
		
		fFileSearchActionInWorkingSet = new FileSearchActionInWorkingSet(getSelectionProvider());
		
		fSearchDialogAction = new SearchDialogAction(getSelectionProvider(), this);
	}

	public void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);

		addGroup(menu, ITextEditorActionConstants.GROUP_EDIT, IContextMenuConstants.GROUP_REORGANIZE);
		addGroup(menu, ITextEditorActionConstants.GROUP_EDIT, IContextMenuConstants.GROUP_GENERATE);
		addGroup(menu, ITextEditorActionConstants.GROUP_EDIT, IContextMenuConstants.GROUP_NEW);

		// Code formatting menu items -- only show in C perspective
		addAction(menu, ITextEditorActionConstants.GROUP_EDIT, "Comment"); //$NON-NLS-1$
		addAction(menu, ITextEditorActionConstants.GROUP_EDIT, "Uncomment"); //$NON-NLS-1$
		// @@@ disabled for now until we get it to do something...
		//addAction(menu, ITextEditorActionConstants.GROUP_EDIT, "Format"); //$NON-NLS-1$

		MenuManager search = new MenuManager("Search", IContextMenuConstants.GROUP_SEARCH); //$NON-NLS-1$
		menu.appendToGroup(ITextEditorActionConstants.GROUP_FIND, search);
		
		if (SearchDialogAction.canActionBeAdded(getSelectionProvider().getSelection())){
			search.add(fSearchDialogAction);
		}
		
		if (FileSearchAction.canActionBeAdded(getSelectionProvider().getSelection())) {
			MenuManager fileSearch = new MenuManager("File Search");
			fileSearch.add(fFileSearchAction);
			fileSearch.add(fFileSearchActionInWorkingSet);
			search.add(fileSearch);
		}

		addAction(menu, IContextMenuConstants.GROUP_GENERATE, "ContentAssistProposal"); //$NON-NLS-1$
		addAction(menu, IContextMenuConstants.GROUP_GENERATE, "AddIncludeOnSelection"); //$NON-NLS-1$
		addAction(menu, IContextMenuConstants.GROUP_GENERATE, "OpenDeclarations"); //$NON-NLS-1$
	}

	public void setOutlinePageInput(CContentOutlinePage page, IEditorInput input) {
		if (page != null) {
			IWorkingCopyManager manager = CUIPlugin.getDefault().getWorkingCopyManager();
			page.setInput((IWorkingCopy)manager.getWorkingCopy(input));
		}
	}

	/**
	 * The <code>AbstractTextEditor</code> implementation of this 
	 * <code>IWorkbenchPart</code> method creates the vertical ruler and
	 * source viewer. Subclasses may extend.
	 * 
	 * We attach our own mouseDown listener on the menu bar, 
	 * and our own listener for cursor/key/selection events to update cursor position in
	 * status bar.
	 */
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		ISelectionChangedListener sListener = new ISelectionChangedListener() {
			private Runnable fRunnable = new Runnable() {
				public void run() {
					updateStatusField(CTextEditorActionConstants.STATUS_CURSOR_POS);
				}
			};

			private Display fDisplay;

			public void selectionChanged(SelectionChangedEvent event) {
				if (fDisplay == null)
					fDisplay = getSite().getShell().getDisplay();
				fDisplay.asyncExec(fRunnable);
			}
		};
		getSelectionProvider().addSelectionChangedListener(sListener);

		if (isTabConversionEnabled())
			startTabConversion();
			
		IPreferenceStore preferenceStore = getPreferenceStore();
		preferenceStore.addPropertyChangeListener(fPropertyChangeListener);
			
		Preferences preferences = CCorePlugin.getDefault().getPluginPreferences();
		preferences.addPropertyChangeListener(fPropertyChangeListener);
	}

	private IMarker getNextError(int offset, boolean forward) {

		IMarker nextError = null;

		IDocument document = getDocumentProvider().getDocument(getEditorInput());
		int endOfDocument = document.getLength();
		int distance = 0;

		IAnnotationModel model = getDocumentProvider().getAnnotationModel(getEditorInput());
		Iterator e = model.getAnnotationIterator();
		while (e.hasNext()) {
			Annotation a = (Annotation) e.next();
			if (a instanceof CMarkerAnnotation) {
				MarkerAnnotation ma = (MarkerAnnotation) a;
				IMarker marker = ma.getMarker();

				if (MarkerUtilities.isMarkerType(marker, IMarker.PROBLEM)) {
					Position p = model.getPosition(a);
					if (!p.includes(offset)) {

						int currentDistance = 0;

						if (forward) {
							currentDistance = p.getOffset() - offset;
							if (currentDistance < 0)
								currentDistance = endOfDocument - offset + p.getOffset();
						} else {
							currentDistance = offset - p.getOffset();
							if (currentDistance < 0)
								currentDistance = offset + endOfDocument - p.getOffset();
						}

						if (nextError == null || (currentDistance < distance && currentDistance != 0)) {
							distance = currentDistance;
							if (distance == 0)
								distance = endOfDocument;
							nextError = marker;
						}
					}
				}
			}
		}
		return nextError;
	}

	public void gotoError(boolean forward) {

		ISelectionProvider provider = getSelectionProvider();

		if (fStatusLineClearer != null) {
			provider.removeSelectionChangedListener(fStatusLineClearer);
			fStatusLineClearer = null;
		}

		ITextSelection s = (ITextSelection) provider.getSelection();
		IMarker nextError = getNextError(s.getOffset(), forward);

		if (nextError != null) {

			gotoMarker(nextError);

			IWorkbenchPage page = getSite().getPage();

			IViewPart view = view = page.findView("org.eclipse.ui.views.TaskList"); //$NON-NLS-1$
			if (view instanceof TaskList) {
				StructuredSelection ss = new StructuredSelection(nextError);
				((TaskList) view).setSelection(ss, true);
			}

			getStatusLineManager().setErrorMessage(nextError.getAttribute(IMarker.MESSAGE, "")); //$NON-NLS-1$
			fStatusLineClearer = new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					getSelectionProvider().removeSelectionChangedListener(fStatusLineClearer);
					fStatusLineClearer = null;
					getStatusLineManager().setErrorMessage(""); //$NON-NLS-1$
				}
			};
			provider.addSelectionChangedListener(fStatusLineClearer);

		} else {

			getStatusLineManager().setErrorMessage(""); //$NON-NLS-1$

		}
	}
	/*
	 * Get the dektop's StatusLineManager
	 */
	protected IStatusLineManager getStatusLineManager() {
		IEditorActionBarContributor contributor = getEditorSite().getActionBarContributor();
		if (contributor instanceof EditorActionBarContributor) {
			return ((EditorActionBarContributor) contributor).getActionBars().getStatusLineManager();
		}
		return null;
	}

	private void startTabConversion() {
		if (fTabConverter == null) {
			fTabConverter = new TabConverter();
			fTabConverter.setNumberOfSpacesPerTab(getPreferenceStore().getInt(CSourceViewerConfiguration.PREFERENCE_TAB_WIDTH));
			AdaptedSourceViewer asv = (AdaptedSourceViewer) getSourceViewer();
			asv.addTextConverter(fTabConverter);
		}
	}

	private void stopTabConversion() {
		if (fTabConverter != null) {
			AdaptedSourceViewer asv = (AdaptedSourceViewer) getSourceViewer();
			asv.removeTextConverter(fTabConverter);
			fTabConverter = null;
		}
	}

	private boolean isTabConversionEnabled() {
		IPreferenceStore store = getPreferenceStore();
		return store.getBoolean(SPACES_FOR_TABS);
	}

	interface ITextConverter {
		void customizeDocumentCommand(IDocument document, DocumentCommand command);
	};

	static class TabConverter implements ITextConverter {

		private String fTabString = "";
		private int tabRatio = 0;

		public void setNumberOfSpacesPerTab(int ratio) {
			tabRatio = ratio;
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < ratio; i++)
				buffer.append(' ');
			fTabString = buffer.toString();
		}

		public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
			String text = command.text;
			StringBuffer buffer = new StringBuffer();
			final String TAB = "\t";
			// create tokens including the tabs
			StringTokenizer tokens = new StringTokenizer(text, TAB, true);

			int charCount = 0;
			try {
				// get offset of insertion less start of line
				// buffer to determine how many characters
				// are already on this line and adjust tabs accordingly
				charCount = command.offset - (document.getLineInformationOfOffset(command.offset).getOffset());
			} catch (Exception ex) {

			}

			String nextToken = null;
			int spaces = 0;
			while (tokens.hasMoreTokens()) {
				nextToken = tokens.nextToken();
				if (TAB.equals(nextToken)) {
					spaces = tabRatio - (charCount % tabRatio);

					for (int i = 0; i < spaces; i++) {
						buffer.append(' ');
					}

					charCount += spaces;
				} else {
					buffer.append(nextToken);
					charCount += nextToken.length();
				}
			}
			command.text = buffer.toString();
		}
	};

	/* Source code language to display */
	public final static String LANGUAGE_CPP = "CEditor.language.cpp";
	public final static String LANGUAGE_C = "CEditor.language.c";

	/**
	 * Adapted source viewer for CEditor
	 */

	public class AdaptedSourceViewer extends SourceViewer implements ITextViewerExtension {

		private List fTextConverters;
		private String fDisplayLanguage;

		public AdaptedSourceViewer(
			Composite parent,
			IVerticalRuler ruler,
			int styles,
			IOverviewRuler fOverviewRuler,
			boolean isOverviewRulerShowing,
			String language) {
			super(parent, ruler, fOverviewRuler, isOverviewRulerShowing, styles);
			fDisplayLanguage = language;
		}
		public IContentAssistant getContentAssistant() {
			return fContentAssistant;
		}

		/*
		 * @see ITextOperationTarget#doOperation(int)
		 */
		public void doOperation(int operation) {

			if (getTextWidget() == null) {
				return;
			}

			super.doOperation(operation);
		}

		public void insertTextConverter(ITextConverter textConverter, int index) {
			throw new UnsupportedOperationException();
		}

		public void addTextConverter(ITextConverter textConverter) {
			if (fTextConverters == null) {
				fTextConverters = new ArrayList(1);
				fTextConverters.add(textConverter);
			} else if (!fTextConverters.contains(textConverter))
				fTextConverters.add(textConverter);
		}

		public void removeTextConverter(ITextConverter textConverter) {
			if (fTextConverters != null) {
				fTextConverters.remove(textConverter);
				if (fTextConverters.size() == 0)
					fTextConverters = null;
			}
		}

		/*
		 * @see TextViewer#customizeDocumentCommand(DocumentCommand)
		 */
		protected void customizeDocumentCommand(DocumentCommand command) {
			super.customizeDocumentCommand(command);
			if (fTextConverters != null) {
				for (Iterator e = fTextConverters.iterator(); e.hasNext();)
					 ((ITextConverter) e.next()).customizeDocumentCommand(getDocument(), command);
			}
		}

		public void setDisplayLanguage(String language) {
			fDisplayLanguage = language;
		}

		public String getDisplayLanguage() {
			return fDisplayLanguage;
		}
	};

	/*
	 * @see AbstractTextEditor#createSourceViewer(Composite, IVerticalRuler, int)
	 */
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		// Figure out if this is a C or C++ source file
		String filename = getEditorInput().getName();
		boolean c_file = filename.endsWith(".c");

		if (!c_file && filename.endsWith(".h")) {
			// ensure that this .h file is part of a C project & not a CPP project

			IFile file = getInputFile();
			if (file != null) {
				IProject project = file.getProject();
				c_file = !CoreModel.getDefault().hasCCNature(project);
			}
		}
		fAnnotationAccess = createAnnotationAccess();
		ISharedTextColors sharedColors = CUIPlugin.getDefault().getSharedTextColors();

		fOverviewRuler = new OverviewRuler(fAnnotationAccess, VERTICAL_RULER_WIDTH, sharedColors);
		Iterator e = fAnnotationPreferences.getAnnotationPreferences().iterator();
		while (e.hasNext()) {
			AnnotationPreference preference = (AnnotationPreference) e.next();
			if (preference.contributesToHeader())
				fOverviewRuler.addHeaderAnnotationType(preference.getAnnotationType());
		}

		ISourceViewer sourceViewer =
			new AdaptedSourceViewer(
				parent,
				ruler,
				styles,
				fOverviewRuler,
				isOverviewRulerVisible(),
				c_file ? LANGUAGE_C : LANGUAGE_CPP);
		fSourceViewerDecorationSupport =
			new SourceViewerDecorationSupport(sourceViewer, fOverviewRuler, fAnnotationAccess, sharedColors);
		configureSourceViewerDecorationSupport();
		return sourceViewer;
	}

	/**
		 * Creates the annotation access for this editor.
	 * @return the created annotation access
	 */
	protected IAnnotationAccess createAnnotationAccess() {
		return new DefaultMarkerAnnotationAccess(fAnnotationPreferences);
	}

	protected void configureSourceViewerDecorationSupport() {
		Iterator e = fAnnotationPreferences.getAnnotationPreferences().iterator();
		while (e.hasNext())
			fSourceViewerDecorationSupport.setAnnotationPreference((AnnotationPreference) e.next());
		fSourceViewerDecorationSupport.setAnnotationPainterPreferenceKeys(
			DefaultMarkerAnnotationAccess.UNKNOWN,
			TextEditorPreferenceConstants.EDITOR_UNKNOWN_INDICATION_COLOR,
			TextEditorPreferenceConstants.EDITOR_UNKNOWN_INDICATION,
			TextEditorPreferenceConstants.EDITOR_UNKNOWN_INDICATION_IN_OVERVIEW_RULER,
			0);

		fSourceViewerDecorationSupport.setCharacterPairMatcher(fBracketMatcher);
		fSourceViewerDecorationSupport.setMatchingCharacterPainterPreferenceKeys(MATCHING_BRACKETS, MATCHING_BRACKETS_COLOR);

		fSourceViewerDecorationSupport.setCursorLinePainterPreferenceKeys(
			TextEditorPreferenceConstants.EDITOR_CURRENT_LINE,
			TextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR);
		fSourceViewerDecorationSupport.setMarginPainterPreferenceKeys(
			TextEditorPreferenceConstants.EDITOR_PRINT_MARGIN,
			TextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLOR,
			TextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLUMN);
		fSourceViewerDecorationSupport.setSymbolicFontName(getFontPropertyPreferenceKey());
	}

	/** Outliner context menu Id */
	protected String fOutlinerContextMenuId;

	/**
	 * Sets the outliner's context menu ID.
	 */
	protected void setOutlinerContextMenuId(String menuId) {
		fOutlinerContextMenuId = menuId;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextEditor#initializeKeyBindingScopes()
	 */
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String [] { "org.eclipse.cdt.ui.cEditorScope" } );
	}

	/* (non-Javadoc)
	 * @see AbstractTextEditor#affectsTextPresentation(PropertyChangeEvent)
	 */
	protected boolean affectsTextPresentation(PropertyChangeEvent event) {
		CTextTools textTools = CUIPlugin.getDefault().getTextTools();
		AsmTextTools asmTools = CUIPlugin.getDefault().getAsmTextTools();
		return textTools.affectsBehavior(event) || asmTools.affectsBehavior(event);
	}

    /**
     * Handles a property change event describing a change
     * of the C core's preferences and updates the preference
     * related editor properties.
     * 
     * @param event the property change event
     */
    protected void handlePreferencePropertyChanged(org.eclipse.jface.util.PropertyChangeEvent event) {
        if (TRANSLATION_TASK_TAGS.equals(event.getProperty())) {
            ISourceViewer sourceViewer= getSourceViewer();
            if (sourceViewer != null && affectsTextPresentation(event))
                sourceViewer.invalidateTextPresentation();
        }
    }
}
