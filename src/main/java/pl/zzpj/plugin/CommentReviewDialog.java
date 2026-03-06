package pl.zzpj.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CommentReviewDialog extends DialogWrapper {

    public static final int REVIEW_EXIT_CODE = DialogWrapper.NEXT_USER_EXIT_CODE;
    public static final int DELETE_ALL_EXIT_CODE = DialogWrapper.NEXT_USER_EXIT_CODE + 1;

    private final int commentCount;

    public CommentReviewDialog(@Nullable Project project, int commentCount) {
        super(project, true);
        this.commentCount = commentCount;

        setTitle("Comment Reviewer");

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Found " + commentCount + " comments in the project source files. What would you like to do with that?");

        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialogPanel.add(label, BorderLayout.CENTER);

        return dialogPanel;
    }

    @Override
    protected Action[] createActions() {
        DialogWrapperAction reviewAction = new DialogWrapperAction("Review") {
            @Override
            protected void doAction(ActionEvent e) {
                close(REVIEW_EXIT_CODE);
            }
        };

        DialogWrapperAction deleteAllAction = new DialogWrapperAction("Delete All") {
            @Override
            protected void doAction(ActionEvent e) {
                close(DELETE_ALL_EXIT_CODE);
            }
        };

        return new Action[]{reviewAction, deleteAllAction, getCancelAction()};
    }
}
