package pl.zzpj.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
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

        PsiManager psiManager = PsiManager.getInstance(project);

        ProjectFileIndex.getInstance(project).iterateContent(virtualFile -> {

            if (!virtualFile.isDirectory() && !virtualFile.getFileType().isBinary()) {

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

        Messages.showInfoMessage(
                project,
                "There are " + foundComments.size() + " comments in your project`s source files.",
                "Comments Quantity"
        );
    }
}
