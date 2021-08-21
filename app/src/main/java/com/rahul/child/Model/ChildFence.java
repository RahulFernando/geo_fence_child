package com.rahul.child.Model;

import java.util.List;

public class ChildFence {
    private Child Child;
    private List<Fence> Fences;

    public ChildFence(com.rahul.child.Model.Child child, List<Fence> fences) {
        Child = child;
        Fences = fences;
    }

    public com.rahul.child.Model.Child getChild() {
        return Child;
    }

    public void setChild(com.rahul.child.Model.Child child) {
        Child = child;
    }

    public List<Fence> getFences() {
        return Fences;
    }

    public void setFences(List<Fence> fences) {
        Fences = fences;
    }
}
