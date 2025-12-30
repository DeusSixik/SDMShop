#version 150

uniform vec4 ColorModulator;
uniform vec2 u_Size;
uniform float u_CornerSize;
uniform float u_BorderWidth;

// Цвета теперь приходят ДЛЯ КАЖДОГО ПИКСЕЛЯ свои
in vec2 texCoord;
in vec4 fillColor;
in vec4 borderColor;

out vec4 fragColor;

void main() {
    vec2 pos = (texCoord - 0.5) * u_Size;
    vec2 absPos = abs(pos);
    vec2 halfSize = u_Size * 0.5;

    // 1. ВЫРЕЗАНИЕ УГЛОВ
    vec2 cornerStart = halfSize - vec2(u_CornerSize);
    if (absPos.x > cornerStart.x && absPos.y > cornerStart.y) {
        discard;
    }

    // 2. ЦВЕТ
    vec4 finalColor = fillColor;

    if (u_BorderWidth > 0.0) {
        bool isBorder = false;

        if (absPos.x > halfSize.x - u_BorderWidth) isBorder = true;
        if (absPos.y > halfSize.y - u_BorderWidth) isBorder = true;
        if (absPos.x > cornerStart.x - u_BorderWidth &&
        absPos.y > cornerStart.y - u_BorderWidth) {
            isBorder = true;
        }

        if (isBorder) {
            finalColor = borderColor;
        }
    }

    fragColor = finalColor * ColorModulator;
}