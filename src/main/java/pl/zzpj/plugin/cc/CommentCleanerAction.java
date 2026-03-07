package pl.zzpj.plugin.cc;

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

public class CommentCleanerAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        List<SmartPsiElementPointer<PsiComment>> detectedComments = new ArrayList<>();

        ProgressManager.getInstance().run(new Task.Modal(project, "Searching for Comments", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);

                ReadAction.run(() -> {
                    PsiManager psiManager = PsiManager.getInstance(project);
                    ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(project);

                    SmartPointerManager pointerManager = SmartPointerManager.getInstance(project);

                    fileIndex.iterateContent(virtualFile -> {
                        indicator.checkCanceled();

                        if (!virtualFile.isDirectory() && fileIndex.isInSourceContent(virtualFile)) {
                            PsiFile psiFile = psiManager.findFile(virtualFile);

                            if (psiFile != null) {
                                psiFile.accept(new PsiRecursiveElementWalkingVisitor() {
                                    @Override
                                    public void visitElement(@NotNull PsiElement element) {
                                        if (element instanceof PsiComment) {
                                            detectedComments.add(pointerManager.createSmartPsiElementPointer((PsiComment) element));
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

        if (detectedComments.isEmpty()) {
            Messages.showInfoMessage(project, "No comments found in the source files. Your code doesn't need cleaning :)", "Search Complete");
            return;
        }

        CommentCleanerDialog dialog = new CommentCleanerDialog(project, detectedComments.size());
        dialog.show();

        switch (dialog.getExitCode()) {
            case CommentCleanerDialog.REVIEW_EXIT_CODE:
                SingleCommentReviewDialog reviewDialog = new SingleCommentReviewDialog(project, detectedComments);
                reviewDialog.show();
                break;

            case CommentCleanerDialog.DELETE_ALL_EXIT_CODE:
                int[] deletedCount = {0};

                WriteCommandAction.runWriteCommandAction(project, "Delete All Comments", "CommentReviewer", () -> {
                    for (SmartPsiElementPointer<PsiComment> pointer : detectedComments) {
                        try {
                            PsiComment comment = pointer.getElement();
                            if (comment != null && comment.isValid() && comment.isWritable()) {
                                comment.delete();
                                deletedCount[0]++;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                });

                Messages.showInfoMessage(project, "Successfully deleted " + deletedCount[0] + " comments. Your code is now clean ;)", "Success");
                break;

            default:
                break;
        }
    }
}