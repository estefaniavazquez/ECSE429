package interoperability.models;

public class JsonTodo {
    private Todo[] todos;

    public JsonTodo() {
    }

    public JsonTodo(Todo[] todos) {
        this.todos = todos;
    }

    public Todo[] getTodos() {
        return todos != null ? todos.clone() : new Todo[0];
    }

    public boolean isIn(Todo todo) {
        if (todos == null)
            return false;
        for (Todo t : todos) {
            if (t.equals(todo)) {
                return true;
            }
        }
        return false;
    }

    public boolean areIn(Todo[] todos) {
        if (todos == null)
            return true;
        for (Todo todo : todos) {
            if (!isIn(todo)) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(Todo todo) {
        return isIn(todo);
    }

    public boolean containsAll(Todo[] todos) {
        return areIn(todos);
    }

    public int size() {
        return todos != null ? todos.length : 0;
    }
}