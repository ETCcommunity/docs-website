package com.sysgears.theme

import com.sysgears.grain.taglib.Site

import java.util.regex.Matcher

/**
 * Change pages urls and extend models.
 */
class ResourceMapper {

    /**
     * Site reference, provides access to site configuration.
     */
    private final Site site

    public ResourceMapper(Site site) {
        this.site = site
    }

    /**
     * This closure is used to transform page URLs and page data models.
     */
    def map = { resources ->

        resources.each {
            println([it.location, it.keySet()].join(' '))
        }

        def refinedResources = resources.findResults(filterPublished).collect { Map resource ->
            setupEcip << fillDates << resource
        }

        refinedResources
    }

    /**
     * Excludes resources with published property set to false,
     * unless it is allowed to show unpublished resources in SiteConfig.
     */
    private def filterPublished = { Map it ->
        (it.published != false || site.show_unpublished) ? it : null
    }

    /**
     * Fills in page `date` and `updated` fields 
     */
    private def fillDates = { Map it ->
        def update = [date: it.date ? Date.parse(site.datetime_format, it.date) : new Date(it.dateCreated as Long),
                updated: it.updated ? Date.parse(site.datetime_format, it.updated) : new Date(it.lastUpdated as Long)]
        it + update
    }

    private def setupEcip = { Map it ->
        def update = [:]
        if (it.location.startsWith('/ECIPs')) {
            update['ecip'] = false
            if (it.location.endsWith('.md')) {
                String content = it.text()
                def mapping = [
                        'ecip': 'id'
                ]
                content.split('\n').each {
                    String line = it.trim()
                    Matcher m = line =~ /(\w+):\s*(.+)$/
                    if (m.matches()) {
                        String id = m.group(1).toLowerCase().replace(' ', '_')
                        String name = ["ecip", mapping[id] ?: id].join('_')
                        update[name] = m.group(2)
                    }
                }
                update['ecip'] = update['ecip_id'] != null && update['ecip_title'] != null
            }
            if (update.ecip) {
                update['title'] = ['ECIP-', update.ecip_id, ':', update.ecip_title].join('')
                update['layout'] = 'ecip'
                update['github_url'] = [
                        'https://github.com/ethereumproject/ECIPs/blob/master',
                        it.location.substring('/ECIPs'.length())
                ].join('')
                Matcher m = it.location =~ /\/(ECIP-\d+).md/
                if (m.find()) {
                    update['url'] = ['/ecips/', m.group(1).toLowerCase(), '.html'].join('')
                }
            } else if (it.markup == 'binary') {
                Matcher m = it.location =~ /\/([^\/]+)$/
                if (m.find()) {
                    update['url'] = ['/ecips/', m.group(1)].join('')
                    println update['url']
                }
            }
        }
        it + update
    }
}
