package pl.zzpj.plugin;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiComment;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class SingleCommentReviewDialog extends DialogWrapper {

    private final Project project;
    private final List<PsiComment> comments;
    private int currentIndex = 0;

    private JPanel centerPanel;
    private Editor currentEditor;

    public SingleCommentReviewDialog(Project project, List<PsiComment> comments) {
        super(project, true);
        this.project = project;
        this.comments = comments;

        setModal(true);

        init();
        showCurrentComment();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(800, 600));
        return centerPanel;
    }

    @Override
    protected Action[] createActions() {
        DialogWrapperAction deleteAction = new DialogWrapperAction("Delete") {
            @Override
            protected void doAction(ActionEvent e) {
                deleteCurrentComment();
            }
        };

        DialogWrapperAction skipAction = new DialogWrapperAction("Skip") {
            @Override
            protected void doAction(ActionEvent e) {
                currentIndex++;
                showCurrentComment();
            }
        };

        return new Action[]{deleteAction, skipAction, getCancelAction()};
    }

    private void showCurrentComment() {
        if (currentIndex >= comments.size()) {
            Messages.showInfoMessage(project, "All comments reviewed!", "Done");
            close(OK_EXIT_CODE);
            return;
        }

        PsiComment comment = comments.get(currentIndex);

        if (!comment.isValid()) {
            currentIndex++;
            showCurrentComment();
            return;
        }

        if (currentEditor != null) {
            EditorFactory.getInstance().releaseEditor(currentEditor);
            centerPanel.removeAll();
        }

        Document document = FileDocumentManager.getInstance().getDocument(comment.getContainingFile().getVirtualFile());
        if (document == null) {
            currentIndex++;
            showCurrentComment();
            return;
        }

        currentEditor = EditorFactory.getInstance().createViewer(document, project);

        centerPanel.add(currentEditor.getComponent(), BorderLayout.CENTER);
        centerPanel.validate();
        centerPanel.repaint();

        int startOffset = comment.getTextRange().getStartOffset();
        int endOffset = comment.getTextRange().getEndOffset();

        TextAttributes highlightAttributes = EditorColorsManager.getInstance()
                .getGlobalScheme().getAttributes(EditorColors.SEARCH_RESULT_ATTRIBUTES);

        currentEditor.getMarkupModel().addRangeHighlighter(
                startOffset, endOffset, HighlighterLayer.SELECTION - 1,
                highlightAttributes, HighlighterTargetArea.EXACT_RANGE
        );


        Runnable scrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentEditor == null || currentEditor.isDisposed()) {
                    return;
                }

                if (!currentEditor.getComponent().isShowing() || currentEditor.getComponent().getWidth() == 0) {
                    ApplicationManager.getApplication().invokeLater(this, ModalityState.any());
                    return;
                }

                currentEditor.getCaretModel().moveToOffset(startOffset);
                currentEditor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
            }
        };

        ApplicationManager.getApplication().invokeLater(scrollRunnable, ModalityState.any());

        setTitle("Reviewing Comment " + (currentIndex + 1) + " of " + comments.size());
    }

    private void deleteCurrentComment() {
        PsiComment comment = comments.get(currentIndex);
        if (comment.isValid() && comment.isWritable()) {
            WriteCommandAction.runWriteCommandAction(project, "Delete Comment", "CommentReviewer", comment::delete);
        }

        currentIndex++;
        showCurrentComment();
    }

    @Override
    public void dispose() {
        if (currentEditor != null) {
            EditorFactory.getInstance().releaseEditor(currentEditor);
        }
        super.dispose();
    }
}