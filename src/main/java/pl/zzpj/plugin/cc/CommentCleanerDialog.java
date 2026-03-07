package pl.zzpj.plugin.cc;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
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
        JLabel label = new JLabel("<html>" +
                "Found " + commentCount + " comments in the project source files. " +
                "You can now either Delete All these comments or Review them one by one and decide what to do with each." +
                "</html>");

        label.setPreferredSize(new Dimension(400, 100));
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
