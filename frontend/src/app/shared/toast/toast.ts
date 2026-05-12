import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, OnDestroy, Output, SimpleChanges } from '@angular/core';

export type ToastVariant = 'success' | 'error' | 'info';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast.html',
  styleUrl: './toast.css',
})
export class ToastComponent implements OnChanges, OnDestroy {
  @Input() open = false;
  @Input() message = '';
  @Input() variant: ToastVariant = 'info';
  @Input() durationMs = 3500;

  @Output() closed = new EventEmitter<void>();

  private closeTimer: ReturnType<typeof setTimeout> | undefined;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['open']) {
      if (this.open) this.scheduleClose();
      else this.clearTimer();
    }

    if (changes['durationMs'] && this.open) {
      this.scheduleClose();
    }
  }

  ngOnDestroy(): void {
    this.clearTimer();
  }

  dismiss(): void {
    if (!this.open) return;
    this.open = false;
    this.clearTimer();
    this.closed.emit();
  }

  private scheduleClose(): void {
    this.clearTimer();

    if (!this.durationMs || this.durationMs <= 0) return;

    this.closeTimer = setTimeout(() => {
      this.dismiss();
    }, this.durationMs);
  }

  private clearTimer(): void {
    if (this.closeTimer) clearTimeout(this.closeTimer);
    this.closeTimer = undefined;
  }
}
