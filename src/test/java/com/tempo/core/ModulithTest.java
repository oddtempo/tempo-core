package com.tempo.core;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModulithTest {

    ApplicationModules modules = ApplicationModules.of(TempoApplication.class);

    @Test
    void verifyModulith() {
        // Kiểm tra cấu trúc module, sự phụ thuộc chéo và encapsulation
        modules.verify();
    }

    @Test
    void writeDocumentation() {
        // Xuất tài liệu kiến trúc (Diagrams, Canvas) vào thư mục build/modulith
        new Documenter(modules).writeDocumentation();
    }
}
