import { CommonModule } from '@angular/common';
import { Component, Input, computed, inject, signal } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-map-embed',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './map-embed.html',
  styleUrl: './map-embed.css',
})
export class MapEmbedComponent {
  private readonly sanitizer = inject(DomSanitizer);

  private readonly latSig = signal<number | null | undefined>(null);
  private readonly lngSig = signal<number | null | undefined>(null);

  @Input()
  set lat(value: number | null | undefined) {
    this.latSig.set(value);
  }
  get lat(): number | null | undefined {
    return this.latSig();
  }

  @Input()
  set lng(value: number | null | undefined) {
    this.lngSig.set(value);
  }
  get lng(): number | null | undefined {
    return this.lngSig();
  }

  @Input() title: string = '';

  @Input() showTitle: boolean = false;
  @Input() showDetails: boolean = true;
  @Input() minHeight: number = 260;

  readonly hasCoordinates = computed(() => {
    const lat = this.latSig();
    const lng = this.lngSig();

    return (
      typeof lat === 'number' &&
      Number.isFinite(lat) &&
      typeof lng === 'number' &&
      Number.isFinite(lng)
    );
  });

  readonly embedUrl = computed<SafeResourceUrl | null>(() => {
    if (!this.hasCoordinates()) return null;

    const lat = this.latSig() as number;
    const lng = this.lngSig() as number;

    // A small bbox around the marker so OSM renders an appropriate zoom.
    const offset = 0.01;
    const left = lng - offset;
    const right = lng + offset;
    const top = lat + offset;
    const bottom = lat - offset;

    const url = `https://www.openstreetmap.org/export/embed.html?bbox=${left}%2C${bottom}%2C${right}%2C${top}&marker=${lat}%2C${lng}&layer=mapnik`;
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  });

  getOpenStreetMapLink(): string | null {
    if (!this.hasCoordinates()) return null;

    const lat = this.latSig() as number;
    const lng = this.lngSig() as number;

    return `https://www.openstreetmap.org/?mlat=${lat}&mlon=${lng}#map=15/${lat}/${lng}`;
  }
}
