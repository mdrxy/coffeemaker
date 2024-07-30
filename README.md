# CoffeeMaker: Full-Stack Web Application

**Line Coverage**

![Coverage](.github/badges/jacoco.svg)

**Branch Coverage**

![Branches](.github/badges/branches.svg)

**Technologies Used:** Java, Spring Boot, Hibernate, Maven, MySQL, Semantic UI [(community fork)](https://github.com/fomantic/Fomantic-UI), HTML, CSS, and deployed w/ CI (GitHub Actions)

## Overview

Developed a full-stack web application in a team of 3 as an introduction to basic full-stack web development principles. The project explores various modern technologies and methodologies to help understand the considerations needed when delivering a robust and scalable application.

* Test-driven development
* [Unit testing](https://github.com/mdrxy/coffeemaker/tree/main/CoffeeMaker/src/test/java/edu/ncsu/csc/CoffeeMaker)
* [Sequence](https://github.com/mdrxy/coffeemaker/wiki/Dynamic-Design) and [class diagramming (UML)](https://github.com/mdrxy/coffeemaker/wiki/Static-Design)
* [Backend](https://github.com/mdrxy/coffeemaker/tree/main/CoffeeMaker/src/main/java/edu/ncsu/csc/CoffeeMaker/models) and [frontend design](https://github.com/mdrxy/coffeemaker/tree/main/images/UC4-Scenario-1) and [implementation](https://github.com/mdrxy/coffeemaker/tree/main/CoffeeMaker/src/main/resources/templates)
* End point design
* [REST API implementation (CRUD)](https://github.com/mdrxy/coffeemaker/tree/main/CoffeeMaker/src/main/java/edu/ncsu/csc/CoffeeMaker/controllers)
* Task & dependency testing

## Team Collaboration + Development Schedule

Our team followed a structured development schedule to ensure systematic progress and effective collaboration. Here’s a detailed breakdown of our milestones:

* **Milestone 0:** Debugging Workshop & Requirements
* **Milestone 1:** Testing Workshop
  * Database & Persistence Testing Workshop
  * API Testing
* **Milestone 2:** Backend Design Workshop
* **Milestone 3:** Frontend Design Workshop
* **Milestone 4:** Backend Implement and Test Workshop
  * Databases and Persistence Implementation
  * REST API Implementation
* **Milestone 5:** Frontend Implement and Test
  * Frontend Workshop
* **Milestone 6:** Project Deployment & Presentation
* **Milestone 7:** Project Reflection & Team Evaluation

## Screenshots

![](/images/screenshots/homepage.png)
![](/images/screenshots/add-ingredient.png)
![](/images/screenshots/add-recipe.png)
![](/images/screenshots/edit-recipe.png)
![](/images/screenshots/edit-error.png)
![](/images/screenshots/edit-success.png)
![](/images/screenshots/update-inventory.png)

## Prerequisites

Assuming installation on macOS, [install MySQL with Homebrew](https://formulae.brew.sh/formula/mysql#default) and then run:

```sh
brew services start mysql
```

Make a copy of `src/main/resources/application.yml.template` inserting the local MySQL password. Run as Java Project in VSCode.
