package pl.bartlomiejstepien.armaserverwebgui.web.validator;

public interface Validator<T>
{
    void validate(T value);
}
