import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/sign_in')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/sign_in"!</div>
}
