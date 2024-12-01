[Android At Scale] nicely lays out why having a small graph height is important to build performance.

To achieve that, we should aim to use `public` and `impl` modules for each `data`, `domain`, and `service` when possible.

It doesn't currently make sense to do this for `destination` modules since no one depends on them aside for `nav` and `app`.

Because of how the scoping is set up in [Template DI] all of the graphs are created in the `app` module, so that is the only
module that implementation details are exposed (technically `destination` implementation details are exposed in `nav`, but
that is OK since `nav` is really just an extension of `app`).

```mermaid
graph TD;
    app{{<span style='text-decoration:underline'>app</span><br/>TemplateApplication<br/>TemplateActivity}};
    common{{<span style='text-decoration:underline'>common</span><br/>strings<br/>drawables}};
    data-impl{{<span style='text-decoration:underline'>data-impl</span><br/>RealRepositories<br/>RealDataSources}};
    data-public{{<span style='text-decoration:underline'>data-public</span><br/>Repositories}};
    destinations{{<span style='text-decoration:underline'>destinations</span><br/>VICEs}};
    di(<span style='text-decoration:underline'>di</span><br/>ActivityScope<br/>DestinationScope<br/>NavScope<br/>AppContext<br/>ActivityContext);
    domain-impl{{<span style='text-decoration:underline'>domain-impl</span><br/>RealUseCases}};
    domain-public{{<span style='text-decoration:underline'>domain-public</span><br/>UseCases}};
    nav(<span style='text-decoration:underline'>nav</span><br/>NavGraphBuilder);
    services-impl{{<span style='text-decoration:underline'>services-impl</span><br/>RealAPI<br/>RealDatabase<br/>RealSplashScreen<br/>RealWorkManager<br/>etc...}};
    services-public{{<span style='text-decoration:underline'>services-public</span><br/>API<br/>Database<br/>SplashScreen<br/>WorkManager<br/>etc...}};
    ui{{<span style='text-decoration:underline'>ui</span><br/>compose<br/>icons<br/>material}};
    app-->services-impl-->services-public;
    app-->data-impl-->data-public;
    app-->domain-impl-->domain-public;
    app-->nav-->destinations;
    data-public-->services-public;
    services-public-->di;
    data-public-->di;
    domain-public-->data-public;
    domain-public-->di;
    destinations-->domain-public;
    destinations-->di;
    destinations-->common;
    destinations-->ui;
```

[Android At Scale]: https://www.droidcon.com/2019/11/15/android-at-scale-square/
[Template DI]: ./DI.md#template-di
