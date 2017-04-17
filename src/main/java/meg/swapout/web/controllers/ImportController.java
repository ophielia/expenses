package meg.swapout.web.controllers;

import meg.swapout.imp.ImportManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class ImportController {

    private final ImportManager importManager;


    @Autowired
    public ImportController(ImportManager importManager) {
        this.importManager = importManager;
    }

    @RequestMapping(value="/import", method = RequestMethod.GET)
    public String listUploadedFiles(Model model) throws IOException {
        return "uploadfile";

    }

    @RequestMapping(value="/import", method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        try {
            importManager.importTransactions(file);
        } catch (IOException e) {
            e.printStackTrace();
            // oops - didn't work
        }

        return "redirect:/ruleassignment";
    }


}