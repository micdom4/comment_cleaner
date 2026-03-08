package pl.zzpj.plugin.cc;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CommentCleanerDialog extends DialogWrapper {

    public static final int REVIEW_EXIT_CODE = DialogWrapper.NEXT_USER_EXIT_CODE;
    public static final int DELETE_ALL_EXIT_CODE = DialogWrapper.NEXT_USER_EXIT_CODE + 1;

    private final int commentCount;

    public CommentCleanerDialog(@Nullable Project project, int commentCount) {
        super(project, true);
        this.commentCount = commentCount;

        setTitle("Comment Cleaner");

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());

        JLabel label = new JLabel(
            """
            <html>
            Found %d comments in the project source files. You can now either \
            Delete All these comments or Review them one by one and decide what to do with each.
            </html>
            """.formatted(commentCount));

        label.setPreferredSize(new Dimension(400, 100));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialogPanel.add(label, BorderLayout.CENTER);

        return dialogPanel;
    }

    @Override
    protected Action[] createActions() {
        return new Action[]{getReviewAction(), getDeleteAllAction(), getCancelAction()};
    }

    private @NotNull DialogWrapperAction getDeleteAllAction() {
        DialogWrapperAction deleteAllAction = new DialogWrapperAction("Delete All") {
            @Override
            protected void doAction(ActionEvent e) {
                int confirmationResult = Messages.showYesNoDialog(
                        "Are you sure you want to delete all " + commentCount + " comments? This action cannot be undone.",
                        "Confirm Delete All",
                        "Delete All",
                        "Cancel",
                        Messages.getWarningIcon()
                );

                if (confirmationResult == Messages.YES) {
                    close(DELETE_ALL_EXIT_CODE);
                }
            }
        };
        return deleteAllAction;
    }

    private @NotNull DialogWrapperAction getReviewAction() {
        DialogWrapperAction reviewAction = new DialogWrapperAction("Review") {
            @Override
            protected void doAction(ActionEvent e) {
                close(REVIEW_EXIT_CODE);
            }
        };
        return reviewAction;
    }
}