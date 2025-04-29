module ReCHor {
    requires javafx.controls;
    requires java.net.http;
    requires jdk.unsupported.desktop;

    exports ch.epfl.rechor;
    exports ch.epfl.rechor.timetable;
    exports ch.epfl.rechor.gui;
    exports ch.epfl.rechor.journey;
    exports ch.epfl.rechor.timetable.mapped;
    exports ch.epfl.gui_debug;
}