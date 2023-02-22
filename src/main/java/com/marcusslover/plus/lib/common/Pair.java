package com.marcusslover.plus.lib.common;

public class Pair<A, B> {

    private A a;
    private B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public static Pair<?, ?> fromString(String s) {
        String[] nums = s.split(",");
        return new Pair<>(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]));
    }

    public static <A, B> Pair<A, B> of(A a, B b) {
        return new Pair<>(a, b);
    }

    public A getA() {
        return this.a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public A getFirst() {
        return this.a;
    }

    public void setFirst(A a) {
        this.a = a;
    }

    public B getSecond() {
        return this.b;
    }

    public B getLast() {
        return this.b;
    }

    public A getLeft() {
        return this.a;
    }

    public B getRight() {
        return this.b;
    }

    public B getB() {
        return this.b;
    }

    public void setB(B b) {
        this.b = b;
    }

    public boolean matchOr(Object o) {
        return this.a.equals(o) || this.b.equals(o);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof Pair<?, ?> p)) {
            return false;
        }

        return this.a.equals(p.getA()) && this.b.equals(p.getB());
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = hash * 31 + this.a.hashCode();
        return hash * 31 + this.b.hashCode();
    }

    @Override
    public String toString() {
        return this.a + "," + this.b;
    }

}