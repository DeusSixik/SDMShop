#version 150

in vec3 Position;
in vec4 Color; // Fill Color
in vec2 UV0;
in ivec2 UV1;  // Overlay (хранит Border R, G) - приходит как int
in ivec2 UV2;  // Lightmap (хранит Border B, A) - приходит как int

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec2 texCoord;
out vec4 fillColor;
out vec4 borderColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    texCoord = UV0;
    fillColor = Color;

    // Восстанавливаем цвет обводки из двух целочисленных векторов
    // Делим на 255.0, чтобы получить диапазон 0..1
    float bR = float(UV1.x) / 255.0;
    float bG = float(UV1.y) / 255.0;
    float bB = float(UV2.x) / 255.0;
    float bA = float(UV2.y) / 255.0;

    borderColor = vec4(bR, bG, bB, bA);
}