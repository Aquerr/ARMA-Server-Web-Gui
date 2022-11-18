package pl.bartlomiejstepien.armaserverwebgui.controller.validator;

public interface Validator<T>
{
    void validate(T value);
}
