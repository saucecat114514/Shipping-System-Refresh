// 高德地图API类型声明
declare global {
  interface Window {
    AMap: typeof AMap;
  }
}

declare namespace AMap {
  class Map {
    constructor(container: string | HTMLElement, options?: MapOptions);
    setCenter(center: [number, number]): void;
    setZoom(zoom: number): void;
    setFitView(overlays?: any[], immediately?: boolean, margins?: number[]): void;
    on(event: string, handler: Function): void;
    destroy(): void;
    clearMap(): void;
  }

  interface MapOptions {
    zoom?: number;
    center?: [number, number];
    mapStyle?: string;
  }

  class Marker {
    constructor(options: MarkerOptions);
    setMap(map: Map | null): void;
    getPosition(): LngLat;
    on(event: string, handler: Function): void;
  }

  interface MarkerOptions {
    position: [number, number];
    title?: string;
    icon?: Icon;
  }

  class Icon {
    constructor(options: IconOptions);
  }

  interface IconOptions {
    image: string;
    size: Size;
    imageSize: Size;
    imageOffset?: Pixel;
  }

  class Size {
    constructor(width: number, height: number);
  }

  class Pixel {
    constructor(x: number, y: number);
  }

  class InfoWindow {
    constructor(options?: InfoWindowOptions);
    setContent(content: string): void;
    open(map: Map, position: [number, number] | LngLat): void;
    close(): void;
  }

  interface InfoWindowOptions {
    isCustom?: boolean;
    autoMove?: boolean;
  }

  interface LngLat {
    lng: number;
    lat: number;
  }
}

export {}; 