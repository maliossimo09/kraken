import {Component, ElementRef, Inject, InjectionToken, Injector, Input, OnInit, Optional} from '@angular/core';
import {StorageNode} from 'projects/storage/src/lib/entities/storage-node';
import {StorageTreeControlService} from 'projects/storage/src/lib/storage-tree/storage-tree-control.service';
import {ComponentPortal, ComponentType, PortalInjector} from '@angular/cdk/portal';
import {STORAGE_NODE} from 'projects/storage/src/lib/storage-editor/storage-node-editors/storage-node-editor';
import {StorageNodeButtonsComponent} from 'projects/storage/src/lib/storage-menu/storage-node-buttons/storage-node-buttons.component';

export const STORAGE_NODE_BUTTONS = new InjectionToken<ComponentType<any>>('StorageNodeButtons');

@Component({
  selector: 'lib-storage-node',
  templateUrl: './storage-node.component.html',
  styleUrls: ['./storage-node.component.scss']
})
export class StorageNodeComponent {

  public hasChild: boolean;
  public nodeButtons: ComponentPortal<any>;
  public hover = false;

  private _expanded: boolean;
  private _node: StorageNode;

  constructor(public ref: ElementRef,
              public treeControl: StorageTreeControlService,
              private injector: Injector,
              @Inject(STORAGE_NODE_BUTTONS) @Optional() private nodeButtonsType: any /*ComponentType<any>*/) {
  }

  @Input() set node(node: StorageNode) {
    this._node = node;
    this.nodeButtons = new ComponentPortal(this.nodeButtonsType ? this.nodeButtonsType : StorageNodeButtonsComponent, null,
      new PortalInjector(this.injector, new WeakMap([[STORAGE_NODE, this.node]])));
    this.hasChild = this.node.type === 'DIRECTORY';
  }

  @Input() set expanded(expanded: boolean) {
    this._expanded = expanded;
  }

  get node(): StorageNode {
    return this._node;
  }

  get expanded(): boolean {
    return this._expanded;
  }

}
