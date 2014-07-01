
# What this is

Model-view framework to create attractive UI displaying small-size data models.
Examples include debugger settings or  project settings.

# What this is not

It's not a scalable general purpose model-view framework. Say, Qt's model/view
framework is something that can display huge tables, fetching them incrementally.
This one does not try to do so.









# Drill-down

The ILinkPresentationModel (to to be implemented) is a name and a link to another IPresentationModel.
The standard view shows this model as a link, and switches to the references presentation model
on click.





# Concepts
============

- Grid Elements - just things on specific grid.

- View Elements - grid elements with bidirectional link to the presentation model

- Presentation Elements - abstract data model elements.

	 

# View Elements

We mostly use 5-column grid - name, grouper, main content 1, main content 2, buttons.

Each view element takes a presentation model element, and is supposed to do two-way sync
with it.














There is model, which is just Java code. There is presentation model, which is
hierarchical structure using predefined types, that translates from model
to UI. Finally, there's UI. There's a bunch of default UI elements, but it's
also possible to customize UI elements which are used to render elements of
the presentation model, depending on type and hierarhcy level and what not.

GridView - a top level UI component in this package. Shows a hierarchical data model on a grid layout.

IGridElement - part of visual hierarchy. Similar to Composite, except it puts every child in a master
grid

IViewModel - a part of presentation hierarchy. Contains method to manipulate the data

Domain-specific-model - is 

# Adapters

- There is an adapter to cut model hierarhcy to specificy depth

- There's a way to create new element factory 


# Design questions

Do we ever need to regarget view to another model? It will complicate things quite a bit
since each model element can be rendered by different view model.

## How do we implement 'SSH' property? It must be possible to 'probe' it. New type?

# How breadcrumbs works

- Modify the factory to generate summary elements with links at some level.

- Activating the link should switch the breadcrumbs. Preferably keeping all
  column widths.
  
  Maybe, we need to create UI for entire hierarchy, but also selectively hide
  some of it and use breadcrumbs to navigate.

- Options:
	 - Have breadcru
