package pl.zzpj.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReviewCommentsAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        List<PsiComment> foundComments = new ArrayList<>();

        ProgressManager.getInstance().run(new Task.Modal(project, "Scanning for Comments", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                ReadAction.run(() -> {
                    PsiManager psiManager = PsiManager.getInstance(project);
                    ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(project);

                    fileIndex.iterateContent(virtualFile -> {
                        indicator.checkCanceled();

                        if (!virtualFile.isDirectory() && fileIndex.isInSourceContent(virtualFile)) {
                            PsiFile psiFile = psiManager.findFile(virtualFile);

                            if (psiFile != null) {
                                psiFile.accept(new PsiRecursiveElementWalkingVisitor() {
                                    @Override
                                    public void visitElement(@NotNull PsiElement element) {
                                        if (element instanceof PsiComment) {
                                            foundComments.add((PsiComment) element);
                                        }
                                        super.visitElement(element);
                                    }
                                });
                            }
                        }
                        return true;
                    });
                });
            }
        });

        if (foundComments.isEmpty()) {
            Messages.showInfoMessage(project, "No comments found in the source files.", "Scan Complete");
            return;
        }

        CommentReviewDialog dialog = new CommentReviewDialog(project, foundComments.size());
        dialog.show();

        switch (dialog.getExitCode()) {
            case CommentReviewDialog.REVIEW_EXIT_CODE:
                Messages.showInfoMessage(project, "Review mode coming soon!", "Action");
                break;

            case CommentReviewDialog.DELETE_ALL_EXIT_CODE:
                int[] deletedCount = {0};

                WriteCommandAction.runWriteCommandAction(project, "Delete All Comments", "CommentReviewer", () -> {
                    for (PsiComment comment : foundComments) {
                        try {
                            if (comment.isValid() && comment.isWritable()) {
                                comment.delete();
                                deletedCount[0]++;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                });

                Messages.showInfoMessage(project, "Successfully deleted " + deletedCount[0] + " comments.", "Success");
                break;

            default:
                break;
        }
    }
}