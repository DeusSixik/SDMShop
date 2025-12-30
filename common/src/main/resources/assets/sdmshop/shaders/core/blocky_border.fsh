#version 150

uniform vec4 ColorModulator;

uniform vec2 u_Size;
uniform float u_CornerSize;
uniform float u_BorderWidth;
uniform vec4 u_FillColor;
uniform vec4 u_BorderColor;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 pos = (texCoord - 0.5) * u_Size;
    vec2 absPos = abs(pos);
    vec2 halfSize = u_Size * 0.5;

    /*
        cornerStart is the coordinate where the "emptiness" of the corner begins
    */
    vec2 cornerStart = halfSize - vec2(u_CornerSize);

    /*
        If we go beyond the edge of the slice in both X and Y -> this is an empty corner, and we delete it.
        (This forms a "step")
    */
    if (absPos.x > cornerStart.x && absPos.y > cornerStart.y) {
        discard;
    }

    /*
        COLOR (Stroke or Fill)
    */
    vec4 finalColor = u_FillColor;

    if (u_BorderWidth > 0.0) {
        bool isBorder = false;

        /*
            A. External boundaries (Straight walls)
            If we are at the very edge of the shape in X or Y
        */
        if (absPos.x > halfSize.x - u_BorderWidth) isBorder = true;
        if (absPos.y > halfSize.y - u_BorderWidth) isBorder = true;

        /*
            B. Internal borders (cutout walls) - FIXED
            Logic: if we are in an area that would be "almost" removed (discard),
            but we have moved back by the width of the stroke, then this is the corner frame.
            One condition covers both the vertical bar and the horizontal bar, as well as the connection angle itself.
        */
        if (absPos.x > cornerStart.x - u_BorderWidth &&
        absPos.y > cornerStart.y - u_BorderWidth) {
            isBorder = true;
        }

        if (isBorder) {
            finalColor = u_BorderColor;
        }
    }

    fragColor = finalColor * ColorModulator;
}