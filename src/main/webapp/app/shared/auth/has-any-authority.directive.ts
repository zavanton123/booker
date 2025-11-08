import { Directive, TemplateRef, ViewContainerRef, computed, effect, inject, input } from '@angular/core';

import { AccountService } from 'app/core/auth/account.service';

/**
 * @whatItDoes Conditionally includes an HTML element if current user has any
 * of the authorities passed as the `expression`.
 *
 * @howToUse
 * ```
 *     <some-element *bookerHasAnyAuthority="'ROLE_ADMIN'">...</some-element>
 *
 *     <some-element *bookerHasAnyAuthority="['ROLE_ADMIN', 'ROLE_USER']">...</some-element>
 * ```
 */
@Directive({
  selector: '[bookerHasAnyAuthority]',
})
export default class HasAnyAuthorityDirective {
  public authorities = input<string | string[]>([], { alias: 'bookerHasAnyAuthority' });

  private readonly templateRef = inject(TemplateRef<any>);
  private readonly viewContainerRef = inject(ViewContainerRef);

  constructor() {
    const accountService = inject(AccountService);
    const currentAccount = accountService.trackCurrentAccount();
    const hasPermission = computed(() => currentAccount()?.authorities && accountService.hasAnyAuthority(this.authorities()));

    effect(() => {
      if (hasPermission()) {
        this.viewContainerRef.createEmbeddedView(this.templateRef);
      } else {
        this.viewContainerRef.clear();
      }
    });
  }
}
