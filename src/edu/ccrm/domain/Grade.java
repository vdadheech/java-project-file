package edu.ccrm.domain;

public enum Grade {
    S(10), A(9), B(8), C(7), D(6), E(5), F(0);

    private final int points;

    Grade(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public static Grade fromPoints(double gpa) {
        if (gpa >= 9.5) return S;
        if (gpa >= 8.5) return A;
        if (gpa >= 7.5) return B;
        if (gpa >= 6.5) return C;
        if (gpa >= 5.5) return D;
        if (gpa >= 4.5) return E;
        return F;
    }
}
