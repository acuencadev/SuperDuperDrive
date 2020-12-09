package com.example.superduperdrive;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {

    @FindBy(css="#buttonLogout")
    private WebElement logoutButton;

    @FindBy(css="#nav-notes-tab")
    private WebElement notesTab;

    @FindBy(css="#buttonAddNewNote")
    private WebElement addNoteButton;

    @FindBy(css="#saveNoteButton")
    private WebElement saveNoteButton;

    @FindBy(css="#note-title")
    private WebElement noteTitleField;

    @FindBy(css="#note-description")
    private WebElement noteDescriptionField;

    private JavascriptExecutor executor;

    public HomePage(WebDriver driver) {
        PageFactory.initElements(driver, this);

        executor = (JavascriptExecutor) driver;
    }

    public void logout() {
        this.logoutButton.click();
    }

    public void openNotesTab() {
        this.executor.executeScript("arguments[0].click()", notesTab);
    }

    public void createNote(String title, String description) {
        openNotesTab();

        this.addNoteButton.click();

        this.noteTitleField.sendKeys(title);
        this.noteDescriptionField.sendKeys(description);

        this.saveNoteButton.click();
    }

    public boolean isNoteListed(String title, String description) {
        return this.notesTab.getText().contains(title) &&
                this.notesTab.getText().contains(description);
    }
}
