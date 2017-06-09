import com.sysgears.theme.ResourceMapper
import com.sysgears.theme.taglib.ThemeTagLib

// This setting defines the character encoding of html pages,
// and therefore should match the character encoding of the site files on a filesystem
html_encoding = 'utf-8' // it is passed to the mata charset attribute of the default page layout

// Resource mapper and tag libs.
resource_mapper = new ResourceMapper(site).map
tag_libs = [ThemeTagLib]

features {
    highlight = 'pygments' // 'none', 'pygments'
    markdown = 'txtmark'   // 'txtmark', 'pegdown'
    asciidoc {
        opts = ['source-highlighter': 'coderay',
                'icons': 'font']
    }
}

environments {
    dev {
        log.info 'Development environment is used'
        url = "http://localhost:${jetty_port}"
        show_unpublished = true
    }
    prod {
        log.info 'Production environment is used'
        url = '' // site URL, for example http://www.example.com
        show_unpublished = false
        features {
            minify_xml = false
            minify_html = false
            minify_js = false
            minify_css = false
        }
    }
    cmd {
        features {
            compass = 'none'
            highlight = 'none'
        }
    }
}

python {
    interpreter = 'jython' // 'auto', 'python', 'jython'
    //cmd_candidates = ['python2', 'python', 'python2.7']
    //setup_tools = '2.1'
}

ruby {
    interpreter = 'jruby'   // 'auto', 'ruby', 'jruby'
    //cmd_candidates = ['ruby', 'ruby1.8.7', 'ruby1.9.3', 'user.home/.rvm/bin/ruby']
    //ruby_gems = '2.2.2'
}
