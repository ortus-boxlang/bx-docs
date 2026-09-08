package ortus.boxlang.bxsites.gradle.tasks;

import ortus.boxlang.bxsites.core.BxSitesVerb;

/**
 * Wraps bx-sites' {@code deploy} verb - builds the site, then ships it via
 * whichever {@code target} its own {@code bxsites.yaml}/CLI flags configure
 * (S3, GitHub Pages, FTP/SFTP, rsync, Netlify, Vercel, Cloudflare Pages,
 * local, etc.). Confirmed against {@code Deploy.bx}: the verb itself writes
 * no separate manifest/log file - any local-disk side effect is whatever the
 * chosen target does - so there's no single output artifact to check here;
 * success is judged purely by the absence of an {@code Error:} line.
 */
public abstract class BxSitesDeployTask extends AbstractBxSitesVerbTask {

    @Override
    protected BxSitesVerb verb() {
        return BxSitesVerb.DEPLOY;
    }
}
